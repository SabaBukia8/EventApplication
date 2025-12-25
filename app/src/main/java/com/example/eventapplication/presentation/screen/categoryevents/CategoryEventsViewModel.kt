package com.example.eventapplication.presentation.screen.categoryevents

import androidx.lifecycle.viewModelScope
import com.example.eventapplication.domain.common.Resource
import com.example.eventapplication.domain.model.Category
import com.example.eventapplication.domain.model.CategoryEventsError
import com.example.eventapplication.domain.model.EventFilters
import com.example.eventapplication.domain.model.EventRegistrationStatus
import com.example.eventapplication.domain.model.NetworkError
import com.example.eventapplication.domain.model.SortOption
import com.example.eventapplication.domain.usecase.event.GetCategoriesUseCase
import com.example.eventapplication.domain.usecase.event.GetEventRegistrationStatusUseCase
import com.example.eventapplication.domain.usecase.event.GetEventsByCategoryUseCase
import com.example.eventapplication.presentation.model.FilterType
import com.example.eventapplication.presentation.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryEventsViewModel @Inject constructor(
    private val getEventsByCategoryUseCase: GetEventsByCategoryUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getEventRegistrationStatusUseCase: GetEventRegistrationStatusUseCase
) : BaseViewModel<CategoryEventsState, CategoryEventsEvent, CategoryEventsSideEffect>(
    initialState = CategoryEventsState.Idle
) {

    private var currentCategoryId: Int? = null
    private var currentSortBy: SortOption = SortOption.START_DATE
    private var currentFilterType: FilterType = FilterType.ALL_EVENTS
    private var currentFilters: EventFilters = EventFilters()
    private var registrationStatusJob: kotlinx.coroutines.Job? = null

    override fun onEvent(event: CategoryEventsEvent) {
        when (event) {
            is CategoryEventsEvent.LoadEvents -> loadEvents(event.categoryId)
            is CategoryEventsEvent.OnSortChanged -> changeSorting(event.sortBy)
            is CategoryEventsEvent.OnFilterChanged -> changeFilter(event.filterType)
            is CategoryEventsEvent.OnFilterClicked -> emitSideEffect(CategoryEventsSideEffect.ShowFilterDialog)
            is CategoryEventsEvent.OnEventClicked -> emitSideEffect(CategoryEventsSideEffect.NavigateToEventDetails(event.eventId))
            is CategoryEventsEvent.OnBackClicked -> emitSideEffect(CategoryEventsSideEffect.NavigateBack)
            is CategoryEventsEvent.OnNotificationClicked -> emitSideEffect(CategoryEventsSideEffect.NavigateToNotifications)
            is CategoryEventsEvent.OnRetry -> currentCategoryId?.let { loadEvents(it) }
            is CategoryEventsEvent.OnLocationSelected -> updateLocationFilter(event.location)
            is CategoryEventsEvent.OnDateRangeSelected -> updateDateRangeFilter(event.startDate, event.endDate)
            is CategoryEventsEvent.OnAvailabilityToggled -> updateAvailabilityFilter(event.onlyAvailable)
            is CategoryEventsEvent.OnClearFilters -> clearFilters()
        }
    }

    private fun loadEvents(categoryId: Int) {
        currentCategoryId = categoryId
        updateState { CategoryEventsState.IsLoading(true) }

        viewModelScope.launch {
            try {
                val category = getCategoryById(categoryId)
                if (category == null) {
                    updateState { CategoryEventsState.Error(CategoryEventsError.CategoryNotFound) }
                    emitSideEffect(CategoryEventsSideEffect.ShowError(CategoryEventsError.CategoryNotFound))
                    return@launch
                }

                getEventsByCategoryUseCase(categoryId, currentFilters, currentSortBy).collect { resource ->
                    when (resource) {
                        is Resource.Loader -> {
                            updateState { CategoryEventsState.IsLoading(resource.isLoading) }
                        }
                        is Resource.Success -> {
                            val events = resource.data

                            // Extract unique locations
                            val locations = events
                                .mapNotNull { it.location }
                                .filter { it != "TBA" && it.isNotBlank() }
                                .distinct()
                                .sorted()

                            updateState {
                                CategoryEventsState.Success(
                                    category = category,
                                    events = events,
                                    sortBy = currentSortBy,
                                    filters = currentFilters,
                                    selectedFilter = currentFilterType,
                                    hasNotifications = false,
                                    availableLocations = locations,
                                    isLoadingStatuses = true
                                )
                            }

                            loadRegistrationStatuses(events.map { it.id })
                        }
                        is Resource.Error -> {
                            val error = mapNetworkError(resource.error)
                            updateState { CategoryEventsState.Error(error) }
                            emitSideEffect(CategoryEventsSideEffect.ShowError(error))
                        }
                    }
                }
            } catch (e: Exception) {
                updateState { CategoryEventsState.Error(CategoryEventsError.UnknownError(e.message ?: "Unknown error")) }
                emitSideEffect(CategoryEventsSideEffect.ShowError(CategoryEventsError.UnknownError(e.message ?: "Unknown error")))
            }
        }
    }

    private suspend fun getCategoryById(categoryId: Int): Category? {
        return try {
            getCategoriesUseCase()
                .mapNotNull { resource ->
                    if (resource is Resource.Success) {
                        resource.data.find { it.id == categoryId }
                    } else null
                }
                .firstOrNull()
        } catch (_: Exception) {
            null
        }
    }

    private fun changeSorting(sortBy: SortOption) {
        currentSortBy = sortBy
        currentCategoryId?.let { loadEvents(it) }
    }

    private fun changeFilter(filterType: FilterType) {
        currentFilterType = filterType
        val currentState = state.value
        if (currentState is CategoryEventsState.Success) {
            updateState {
                currentState.copy(selectedFilter = filterType)
            }
        }
    }

    private fun loadRegistrationStatuses(eventIds: List<Int>) {
        registrationStatusJob?.cancel()

        registrationStatusJob = viewModelScope.launch {
            val statusMap = mutableMapOf<Int, EventRegistrationStatus>()

            val jobs = eventIds.map { eventId ->
                async {
                    try {
                        getEventRegistrationStatusUseCase(eventId).firstOrNull()?.let { resource ->
                            if (resource is Resource.Success) {
                                statusMap[eventId] = resource.data
                            }
                        }
                    } catch (e: Exception) {
                    }
                }
            }

            jobs.awaitAll()

            val currentState = state.value
            if (currentState is CategoryEventsState.Success) {
                updateState {
                    currentState.copy(
                        registrationStatuses = statusMap,
                        isLoadingStatuses = false
                    )
                }
            }
        }
    }

    private fun updateLocationFilter(location: String?) {
        currentFilters = currentFilters.copy(location = location)
        currentCategoryId?.let { loadEvents(it) }
    }

    private fun updateDateRangeFilter(startDate: String?, endDate: String?) {
        currentFilters = currentFilters.copy(startDate = startDate, endDate = endDate)
        currentCategoryId?.let { loadEvents(it) }
    }

    private fun updateAvailabilityFilter(onlyAvailable: Boolean) {
        currentFilters = currentFilters.copy(onlyAvailable = onlyAvailable)
        currentCategoryId?.let { loadEvents(it) }
    }

    private fun clearFilters() {
        currentFilters = EventFilters()
        currentCategoryId?.let { loadEvents(it) }
    }

    private fun mapNetworkError(error: NetworkError): CategoryEventsError {
        return when (error) {
            NetworkError.Unauthorized -> CategoryEventsError.UnauthorizedError
            NetworkError.ServerError -> CategoryEventsError.ServerError
            NetworkError.NoInternet -> CategoryEventsError.NetworkError
            NetworkError.Forbidden -> CategoryEventsError.UnknownError(error.toString())
            NetworkError.NotFound -> CategoryEventsError.UnknownError(error.toString())
            NetworkError.Timeout -> CategoryEventsError.NetworkError
            is NetworkError.Unknown -> CategoryEventsError.UnknownError(error.message ?: "Unknown error")
            else -> CategoryEventsError.UnknownError("Unknown error")
        }
    }
}
