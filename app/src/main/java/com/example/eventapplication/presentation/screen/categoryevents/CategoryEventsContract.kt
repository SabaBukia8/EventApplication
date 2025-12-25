package com.example.eventapplication.presentation.screen.categoryevents

import com.example.eventapplication.domain.model.Category
import com.example.eventapplication.domain.model.CategoryEventsError
import com.example.eventapplication.domain.model.Event
import com.example.eventapplication.domain.model.EventFilters
import com.example.eventapplication.domain.model.EventRegistrationStatus
import com.example.eventapplication.domain.model.SortOption
import com.example.eventapplication.presentation.model.FilterType

sealed class CategoryEventsState {
    object Idle : CategoryEventsState()
    data class IsLoading(val isLoading: Boolean) : CategoryEventsState()
    data class Success(
        val category: Category,
        val events: List<Event>,
        val sortBy: SortOption,
        val filters: EventFilters = EventFilters(),
        val selectedFilter: FilterType = FilterType.ALL_EVENTS,
        val hasNotifications: Boolean = false,
        val registrationStatuses: Map<Int, EventRegistrationStatus> = emptyMap(),
        val availableLocations: List<String> = emptyList(),
        val isLoadingStatuses: Boolean = false
    ) : CategoryEventsState()
    data class Error(val error: CategoryEventsError) : CategoryEventsState()
}

sealed class CategoryEventsEvent {
    data class LoadEvents(val categoryId: Int) : CategoryEventsEvent()
    data class OnSortChanged(val sortBy: SortOption) : CategoryEventsEvent()
    data class OnFilterChanged(val filterType: FilterType) : CategoryEventsEvent()
    object OnFilterClicked : CategoryEventsEvent()
    data class OnEventClicked(val eventId: Int) : CategoryEventsEvent()
    object OnBackClicked : CategoryEventsEvent()
    object OnNotificationClicked : CategoryEventsEvent()
    object OnRetry : CategoryEventsEvent()
    data class OnLocationSelected(val location: String?) : CategoryEventsEvent()
    data class OnDateRangeSelected(val startDate: String?, val endDate: String?) : CategoryEventsEvent()
    data class OnAvailabilityToggled(val onlyAvailable: Boolean) : CategoryEventsEvent()
    object OnClearFilters : CategoryEventsEvent()
}

sealed class CategoryEventsSideEffect {
    data class NavigateToEventDetails(val eventId: Int) : CategoryEventsSideEffect()
    object NavigateBack : CategoryEventsSideEffect()
    object NavigateToNotifications : CategoryEventsSideEffect()
    object ShowFilterDialog : CategoryEventsSideEffect()
    data class ShowError(val error: CategoryEventsError) : CategoryEventsSideEffect()
}
