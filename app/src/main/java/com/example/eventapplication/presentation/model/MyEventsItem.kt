package com.example.eventapplication.presentation.model

import com.example.eventapplication.domain.model.UserEventRegistration

sealed class MyEventsItem {
    data class Header(
        val title: String = "My Events"
    ) : MyEventsItem()

    data class EventCard(
        val registration: UserEventRegistration,
        val isNextUpcoming: Boolean = false
    ) : MyEventsItem()

    object Empty : MyEventsItem()
}
