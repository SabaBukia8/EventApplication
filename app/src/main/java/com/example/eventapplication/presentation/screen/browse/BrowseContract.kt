package com.example.eventapplication.presentation.screen.browse

import com.example.eventapplication.domain.model.BrowseError
import com.example.eventapplication.domain.model.Category
import com.example.eventapplication.domain.model.Event
import com.example.eventapplication.domain.model.SortOption

sealed class BrowseState {
    object Idle : BrowseState()
    data class IsLoading(val isLoading: Boolean) : BrowseState()
    data class Success(
        val events: List<Event>,
        val categories: List<Category>,
        val selectedCategoryId: Int?,
        val searchQuery: String,
        val hasActiveFilters: Boolean,
        val filterState: FilterState
    ) : BrowseState()
    data class Error(val error: BrowseError) : BrowseState()
}

sealed class BrowseEvent {
    object LoadData : BrowseEvent()
    data class OnSearchQueryChanged(val query: String) : BrowseEvent()
    data class OnCategorySelected(val categoryId: Int?) : BrowseEvent()
    object OnFilterClicked : BrowseEvent()
    data class OnFilterApplied(val filterState: FilterState) : BrowseEvent()
    data class OnEventClicked(val eventId: Int) : BrowseEvent()
    object OnRetry : BrowseEvent()
    object OnClearFilters : BrowseEvent()
}

sealed class BrowseSideEffect {
    data class NavigateToEventDetails(val eventId: Int) : BrowseSideEffect()
    object ShowFilterDialog : BrowseSideEffect()
    data class ShowError(val error: BrowseError) : BrowseSideEffect()
}

data class FilterState(
    val location: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val showFullEvents: Boolean = true,
    val sortBy: SortOption = SortOption.START_DATE
) {
    fun hasActiveFilters(): Boolean =
        location != null || startDate != null || endDate != null || !showFullEvents
}
