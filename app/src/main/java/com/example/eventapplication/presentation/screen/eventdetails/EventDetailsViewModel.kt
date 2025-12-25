package com.example.eventapplication.presentation.screen.eventdetails

import androidx.lifecycle.viewModelScope
import com.example.eventapplication.R
import com.example.eventapplication.data.local.datastore.DataStoreManager
import com.example.eventapplication.domain.common.Resource
import com.example.eventapplication.domain.model.EventDetailsError
import com.example.eventapplication.domain.model.NetworkError
import com.example.eventapplication.domain.model.RegistrationStatus
import com.example.eventapplication.domain.usecase.auth.CancelRegistrationUseCase
import com.example.eventapplication.domain.usecase.auth.RegisterForEventUseCase
import com.example.eventapplication.domain.usecase.event.GetEventDetailsUseCase
import com.example.eventapplication.domain.util.preferences.PreferenceKeys
import com.example.eventapplication.presentation.common.BaseViewModel
import com.example.eventapplication.presentation.util.StringResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventDetailsViewModel @Inject constructor(
    private val getEventDetailsUseCase: GetEventDetailsUseCase,
    private val registerForEventUseCase: RegisterForEventUseCase,
    private val cancelRegistrationUseCase: CancelRegistrationUseCase,
    private val dataStoreManager: DataStoreManager,
    private val stringProvider: StringResourceProvider
) : BaseViewModel<EventDetailsState, EventDetailsEvent, EventDetailsSideEffect>(
    initialState = EventDetailsState.Idle
) {

    private var currentEventId: Int? = null
    private var currentUserId: Int? = null

    override fun onEvent(event: EventDetailsEvent) {
        android.util.Log.d("EventDetailsViewModel", "onEvent called with: $event")
        when (event) {
            is EventDetailsEvent.LoadEventDetails -> loadEventDetails(event.eventId)
            is EventDetailsEvent.RegisterClicked -> {
                android.util.Log.d("EventDetailsViewModel", "RegisterClicked event received")
                registerForEvent()
            }

            is EventDetailsEvent.CancelRegistrationClicked -> {
                android.util.Log.d(
                    "EventDetailsViewModel",
                    "CancelRegistrationClicked event received"
                )
                cancelRegistration()
            }

            is EventDetailsEvent.BackClicked -> emitSideEffect(EventDetailsSideEffect.NavigateBack)
        }
    }

    private fun loadEventDetails(eventId: Int) {
        currentEventId = eventId
        android.util.Log.d("EventDetailsViewModel", "loadEventDetails called for eventId: $eventId")
        updateState { EventDetailsState.IsLoading(true) }

        viewModelScope.launch {
            val userId =
                dataStoreManager.getPreference(PreferenceKeys.USER_ID, "0").first().toIntOrNull()
            currentUserId = userId
            android.util.Log.d("EventDetailsViewModel", "Retrieved userId: $userId")

            getEventDetailsUseCase(eventId).collect { resource ->
                android.util.Log.d("EventDetailsViewModel", "Received resource: $resource")
                when (resource) {
                    is Resource.Loader -> {
                        android.util.Log.d(
                            "EventDetailsViewModel",
                            "Loading: ${resource.isLoading}"
                        )
                        updateState { currentState ->
                            if (currentState is EventDetailsState.Success || currentState is EventDetailsState.Error) {
                                currentState
                            } else {
                                EventDetailsState.IsLoading(resource.isLoading)
                            }
                        }
                    }

                    is Resource.Success -> {
                        android.util.Log.d(
                            "EventDetailsViewModel",
                            "Success! Event details: ${resource.data}"
                        )
                        updateState {
                            EventDetailsState.Success(
                                eventDetails = resource.data,
                                isRegistering = false
                            )
                        }
                    }

                    is Resource.Error -> {
                        android.util.Log.e(
                            "EventDetailsViewModel",
                            "Error loading event details: ${resource.error}"
                        )
                        updateState { EventDetailsState.Error(EventDetailsError.UnknownError) }
                        emitSideEffect(EventDetailsSideEffect.ShowError(EventDetailsError.UnknownError))
                    }
                }
            }
        }
    }

    private fun registerForEvent() {
        android.util.Log.d("EventDetailsViewModel", "registerForEvent() called")
        android.util.Log.d("EventDetailsViewModel", "currentEventId: $currentEventId")
        android.util.Log.d("EventDetailsViewModel", "currentUserId: $currentUserId")

        val eventId = currentEventId ?: run {
            android.util.Log.e("EventDetailsViewModel", "currentEventId is null!")
            return
        }
        val userId = currentUserId?.takeIf { it > 0 } ?: run {
            android.util.Log.e(
                "EventDetailsViewModel",
                "currentUserId is null or invalid: $currentUserId"
            )
            emitSideEffect(EventDetailsSideEffect.ShowToastResource(R.string.user_not_logged_in))
            return
        }

        val currentState = state.value
        android.util.Log.d("EventDetailsViewModel", "Current state: $currentState")
        if (currentState !is EventDetailsState.Success) {
            android.util.Log.e("EventDetailsViewModel", "State is not Success, returning")
            return
        }

        android.util.Log.d("EventDetailsViewModel", "Setting isRegistering to true")
        updateState {
            (it as EventDetailsState.Success).copy(isRegistering = true)
        }

        viewModelScope.launch {
            android.util.Log.d("EventDetailsViewModel", "Calling registerForEventUseCase")
            registerForEventUseCase(eventId, userId).collect { resource ->
                android.util.Log.d("EventDetailsViewModel", "Registration resource: $resource")
                when (resource) {
                    is Resource.Loader -> {
                        android.util.Log.d(
                            "EventDetailsViewModel",
                            "Loading: ${resource.isLoading}"
                        )
                    }

                    is Resource.Success -> {
                        android.util.Log.d("EventDetailsViewModel", "Registration successful!")
                        emitSideEffect(EventDetailsSideEffect.ShowToastResource(R.string.registration_successful))
                        loadEventDetails(eventId)
                    }

                    is Resource.Error -> {
                        android.util.Log.e(
                            "EventDetailsViewModel",
                            "Registration error: ${resource.error}"
                        )
                        updateState {
                            if (it is EventDetailsState.Success) {
                                it.copy(isRegistering = false)
                            } else it
                        }
                        val errorMessage = when (resource.error) {
                            is NetworkError.Unknown -> resource.error.message
                            NetworkError.NoInternet -> null
                            NetworkError.Unauthorized -> null
                            NetworkError.Forbidden -> null
                            NetworkError.NotFound -> null
                            NetworkError.ServerError -> null
                            NetworkError.Timeout -> null
                            else -> null
                        }

                        when {
                            errorMessage?.contains(
                                "already registered",
                                ignoreCase = true
                            ) == true -> {
                                android.util.Log.d(
                                    "EventDetailsViewModel",
                                    "User already registered, reloading event details"
                                )
                                loadEventDetails(eventId)
                            }

                            errorMessage?.contains("event is full", ignoreCase = true) == true -> {
                                emitSideEffect(EventDetailsSideEffect.ShowToast("Event is now full. Please try joining the waitlist instead."))
                                loadEventDetails(eventId) // Reload to show "Join Waitlist" button
                            }

                            errorMessage != null -> {
                                emitSideEffect(EventDetailsSideEffect.ShowToast(errorMessage))
                            }

                            else -> {
                                emitSideEffect(EventDetailsSideEffect.ShowToastResource(R.string.operation_failed))
                            }
                        }
                    }
                }
            }
        }
    }

    private fun cancelRegistration() {
        val eventId = currentEventId ?: return
        val currentState = state.value
        if (currentState !is EventDetailsState.Success) return

        updateState {
            (it as EventDetailsState.Success).copy(isRegistering = true)
        }

        viewModelScope.launch {
            cancelRegistrationUseCase(eventId).collect { resource ->
                when (resource) {
                    is Resource.Loader -> {
                    }

                    is Resource.Success -> {
                        emitSideEffect(EventDetailsSideEffect.ShowToastResource(R.string.registration_cancelled))
                        loadEventDetails(eventId)
                    }

                    is Resource.Error -> {
                        updateState {
                            if (it is EventDetailsState.Success) {
                                it.copy(isRegistering = false)
                            } else it
                        }
                        val errorMessage = when (resource.error) {
                            is NetworkError.Unknown -> resource.error.message
                            NetworkError.NoInternet -> null
                            NetworkError.Unauthorized -> null
                            NetworkError.Forbidden -> null
                            NetworkError.NotFound -> null
                            NetworkError.ServerError -> null
                            NetworkError.Timeout -> null
                            else -> null
                        }
                        if (errorMessage != null) {
                            emitSideEffect(EventDetailsSideEffect.ShowToast(errorMessage))
                        } else {
                            emitSideEffect(EventDetailsSideEffect.ShowToastResource(R.string.operation_failed))
                        }
                    }
                }
            }
        }
    }

    fun getButtonText(registrationStatus: RegistrationStatus?, isFull: Boolean): String {
        return when {
            registrationStatus == RegistrationStatus.CONFIRMED -> stringProvider.getString(R.string.cancel_registration)
            registrationStatus == RegistrationStatus.WAITLISTED -> stringProvider.getString(R.string.cancel_registration)
            registrationStatus == RegistrationStatus.CANCELLED -> stringProvider.getString(R.string.registration_cancelled_permanent)
            isFull -> stringProvider.getString(R.string.join_waitlist)
            else -> stringProvider.getString(R.string.register_now_button)
        }
    }

    fun isButtonEnabled(registrationStatus: RegistrationStatus?): Boolean {
        // Disable button if user has cancelled registration (permanent block)
        return registrationStatus != RegistrationStatus.CANCELLED
    }
}
