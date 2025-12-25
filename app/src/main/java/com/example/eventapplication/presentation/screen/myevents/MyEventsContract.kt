package com.example.eventapplication.presentation.screen.myevents

import com.example.eventapplication.domain.model.UserEventRegistration

sealed class MyEventsState {
    object Idle : MyEventsState()
    data class IsLoading(val isLoading: Boolean) : MyEventsState()
    data class Success(
        val registrations: List<UserEventRegistration>,
        val nextUpcomingEventId: Int? = null
    ) : MyEventsState()

    object Empty : MyEventsState()
    data class Error(val message: String) : MyEventsState()
}

sealed class MyEventsEvent {
    object LoadRegistrations : MyEventsEvent()
    object OnRefresh : MyEventsEvent()
    data class OnEventClicked(val eventId: Int) : MyEventsEvent()
    object OnCalendarViewClicked : MyEventsEvent()
    object OnRetry : MyEventsEvent()
}

sealed class MyEventsSideEffect {
    data class NavigateToEventDetails(val eventId: Int) : MyEventsSideEffect()
    data class ShowMessage(val message: String) : MyEventsSideEffect()
}
