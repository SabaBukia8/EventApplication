package com.example.eventapplication.presentation.model

import com.example.eventapplication.domain.model.Event
import com.example.eventapplication.domain.model.EventRegistrationStatus


sealed class CategoryEventsItem {
    data class Header(
        val categoryName: String,
        val hasNotifications: Boolean = false,
        val selectedFilter: FilterType = FilterType.ALL_EVENTS,
        val availableLocations: List<String> = emptyList(),
        val selectedLocation: String? = null,
        val dateRangeText: String? = null,
        val onlyAvailable: Boolean = false,
        val hasActiveFilters: Boolean = false
    ) : CategoryEventsItem()

    data class FilterChips(
        val selectedFilter: FilterType = FilterType.ALL_EVENTS
    ) : CategoryEventsItem()

    data class EventCard(
        val event: Event,
        val isRegistered: Boolean = false,
        val isWaitlisted: Boolean = false,
        val registrationStatus: EventRegistrationStatus? = null
    ) : CategoryEventsItem()
}

enum class FilterType {
    ALL_EVENTS,
}
