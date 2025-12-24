package com.example.eventapplication.presentation.screen.browse

import androidx.lifecycle.viewModelScope
import com.example.eventapplication.domain.common.Resource
import com.example.eventapplication.domain.model.BrowseError
import com.example.eventapplication.domain.model.Category
import com.example.eventapplication.domain.model.NetworkError
import com.example.eventapplication.domain.model.SortOption
import com.example.eventapplication.domain.usecase.event.GetCategoriesUseCase
import com.example.eventapplication.domain.usecase.event.GetEventsUseCase
import com.example.eventapplication.presentation.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class BrowseViewModel @Inject constructor(
    private val getEventsUseCase: GetEventsUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase
) : BaseViewModel<BrowseState, BrowseEvent, BrowseSideEffect>(
    initialState = BrowseState.Idle
) {

    private val searchQueryFlow = MutableStateFlow("")
    private var currentFilterState = FilterState()

    init {
        setupSearchDebouncing()
    }

    override fun onEvent(event: BrowseEvent) {
        when (event) {
            is BrowseEvent.LoadData -> loadData()
            is BrowseEvent.OnSearchQueryChanged -> handleSearchQueryChanged(event.query)
            is BrowseEvent.OnCategorySelected -> handleCategorySelected(event.categoryId)
            is BrowseEvent.OnFilterClicked -> emitSideEffect(BrowseSideEffect.ShowFilterDialog)
            is BrowseEvent.OnFilterApplied -> handleFilterApplied(event.filterState)
            is BrowseEvent.OnEventClicked -> navigateToEventDetails(event.eventId)
            is BrowseEvent.OnRetry -> loadData()
            is BrowseEvent.OnClearFilters -> clearFilters()
        }
    }

    private fun setupSearchDebouncing() {
        viewModelScope.launch {
            searchQueryFlow
                .debounce(300)
                .distinctUntilChanged()
                .collect { query ->
                    val currentState = state.value as? BrowseState.Success ?: return@collect
                    loadEvents(
                        searchQuery = query,
                        categoryId = currentState.selectedCategoryId,
                        filterState = currentFilterState
                    )
                }
        }
    }

    private fun loadData() {
        updateState { BrowseState.IsLoading(true) }

        viewModelScope.launch {
            getCategoriesUseCase().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val allCategories = listOf(
                            Category(id = 0, type = null, eventCount = 0)
                        ) + resource.data

                        updateState {
                            BrowseState.Success(
                                events = emptyList(),
                                categories = allCategories,
                                selectedCategoryId = 0,
                                searchQuery = "",
                                hasActiveFilters = false,
                                filterState = FilterState()
                            )
                        }

                        loadEvents(categoryId = 0)
                    }
                    is Resource.Error -> {
                        val browseError = mapNetworkErrorToBrowseError(resource.error)
                        updateState { BrowseState.Error(browseError) }
                        emitSideEffect(BrowseSideEffect.ShowError(browseError))
                    }
                    is Resource.Loader -> {
                        if (state.value !is BrowseState.Success) {
                            updateState { BrowseState.IsLoading(resource.isLoading) }
                        }
                    }
                }
            }
        }
    }

    private fun handleSearchQueryChanged(query: String) {
        val currentState = state.value as? BrowseState.Success ?: return

        updateState {
            currentState.copy(searchQuery = query)
        }

        searchQueryFlow.value = query
    }

    private fun handleCategorySelected(categoryId: Int?) {
        val currentState = state.value as? BrowseState.Success ?: return

        val newId = if (categoryId == null) 0 else categoryId

        if (currentState.selectedCategoryId == newId) return

        updateState {
            currentState.copy(selectedCategoryId = newId)
        }

        loadEvents(
            searchQuery = currentState.searchQuery,
            categoryId = newId,
            filterState = currentFilterState
        )
    }

    private fun handleFilterApplied(filterState: FilterState) {
        currentFilterState = filterState
        val currentState = state.value as? BrowseState.Success ?: return

        updateState {
            currentState.copy(
                filterState = filterState,
                hasActiveFilters = filterState.hasActiveFilters()
            )
        }

        loadEvents(
            searchQuery = currentState.searchQuery,
            categoryId = currentState.selectedCategoryId,
            filterState = filterState
        )
    }

    private fun clearFilters() {
        currentFilterState = FilterState()
        val currentState = state.value as? BrowseState.Success ?: return

        updateState {
            currentState.copy(
                filterState = FilterState(),
                hasActiveFilters = false
            )
        }

        loadEvents(
            searchQuery = currentState.searchQuery,
            categoryId = currentState.selectedCategoryId,
            filterState = FilterState()
        )
    }

    private fun loadEvents(
        searchQuery: String = "",
        categoryId: Int? = null,
        filterState: FilterState = FilterState()
    ) {
        viewModelScope.launch {
            val apiCategoryId = if (categoryId == 0) null else categoryId

            getEventsUseCase(
                eventTypeId = apiCategoryId,
                location = filterState.location,
                startDate = filterState.startDate,
                endDate = filterState.endDate,
                searchKeyword = searchQuery,
                onlyAvailable = if (filterState.showFullEvents) null else true
            ).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val currentState = state.value as? BrowseState.Success ?: return@collect

                        val sortedEvents = when (filterState.sortBy) {
                            SortOption.START_DATE -> resource.data.sortedBy { it.startDateTime }
                            SortOption.POPULARITY -> resource.data.sortedByDescending { it.confirmedCount }
                            SortOption.SPOTS_AVAILABLE -> resource.data.sortedByDescending { it.spotsLeft }
                        }

                        updateState {
                            currentState.copy(events = sortedEvents)
                        }
                    }
                    is Resource.Error -> {
                        val browseError = mapNetworkErrorToBrowseError(resource.error)
                        emitSideEffect(BrowseSideEffect.ShowError(browseError))
                    }
                    is Resource.Loader -> {
                        if (state.value !is BrowseState.Success) {
                            updateState { BrowseState.IsLoading(resource.isLoading) }
                        }
                    }
                }
            }
        }
    }

    private fun navigateToEventDetails(eventId: Int) {
        emitSideEffect(BrowseSideEffect.NavigateToEventDetails(eventId))
    }

    private fun mapNetworkErrorToBrowseError(error: NetworkError): BrowseError =
        when (error) {
            NetworkError.NoInternet -> BrowseError.NetworkError
            NetworkError.Unauthorized -> BrowseError.UnauthorizedError
            NetworkError.ServerError -> BrowseError.ServerError(500)
            NetworkError.Forbidden -> BrowseError.UnauthorizedError
            NetworkError.NotFound -> BrowseError.UnknownError
            NetworkError.Timeout -> BrowseError.NetworkError
            is NetworkError.Unknown -> BrowseError.UnknownError
            else -> BrowseError.UnknownError
        }
}