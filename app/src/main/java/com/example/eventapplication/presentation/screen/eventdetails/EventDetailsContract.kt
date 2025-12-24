package com.example.eventapplication.presentation.screen.eventdetails

import com.example.eventapplication.domain.model.EventDetails
import com.example.eventapplication.domain.model.EventDetailsError

sealed class EventDetailsState {
    object Idle : EventDetailsState()
    data class IsLoading(val isLoading: Boolean) : EventDetailsState()
    data class Success(
        val eventDetails: EventDetails,
        val registrationId: Int? = null,
        val isRegistering: Boolean = false
    ) : EventDetailsState()
    data class Error(val error: EventDetailsError) : EventDetailsState()
}

sealed class EventDetailsEvent {
    data class LoadEventDetails(val eventId: Int) : EventDetailsEvent()
    object RegisterClicked : EventDetailsEvent()
    object CancelRegistrationClicked : EventDetailsEvent()
    object BackClicked : EventDetailsEvent()
}

sealed class EventDetailsSideEffect {
    data class ShowError(val error: EventDetailsError) : EventDetailsSideEffect()
    data class ShowToast(val message: String) : EventDetailsSideEffect() // For backward compatibility
    data class ShowToastResource(@androidx.annotation.StringRes val messageResId: Int) : EventDetailsSideEffect()
    object NavigateBack : EventDetailsSideEffect()
}
