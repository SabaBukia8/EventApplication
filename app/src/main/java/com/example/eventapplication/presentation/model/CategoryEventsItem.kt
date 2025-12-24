package com.example.eventapplication.presentation.model

import com.example.eventapplication.domain.model.Event


sealed class CategoryEventsItem {
    data class Header(
        val categoryName: String,
        val hasNotifications: Boolean = false,
        val selectedFilter: FilterType = FilterType.ALL_EVENTS
    ) : CategoryEventsItem()

    data class FilterChips(
        val selectedFilter: FilterType = FilterType.ALL_EVENTS
    ) : CategoryEventsItem()

    data class EventCard(
        val event: Event,
        val isRegistered: Boolean = false,
        val isWaitlisted: Boolean = false
    ) : CategoryEventsItem()
}

enum class FilterType {
    ALL_EVENTS,
    DATE,
    LOCATION
}
