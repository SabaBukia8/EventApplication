package com.example.eventapplication.presentation.screen.eventdetails

import androidx.lifecycle.viewModelScope
import com.example.eventapplication.R
import com.example.eventapplication.data.local.datastore.DataStoreManager
import com.example.eventapplication.domain.model.EventDetailsError
import com.example.eventapplication.domain.model.NetworkError
import com.example.eventapplication.domain.model.RegistrationStatus
import com.example.eventapplication.domain.usecase.auth.CancelRegistrationUseCase
import com.example.eventapplication.domain.usecase.event.GetEventDetailsUseCase
import com.example.eventapplication.domain.usecase.auth.RegisterForEventUseCase
import com.example.eventapplication.domain.common.Resource
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
        when (event) {
            is EventDetailsEvent.LoadEventDetails -> loadEventDetails(event.eventId)
            is EventDetailsEvent.RegisterClicked -> registerForEvent()
            is EventDetailsEvent.CancelRegistrationClicked -> cancelRegistration()
            is EventDetailsEvent.BackClicked -> emitSideEffect(EventDetailsSideEffect.NavigateBack)
        }
    }

    private fun loadEventDetails(eventId: Int) {
        currentEventId = eventId
        updateState { EventDetailsState.IsLoading(true) }

        viewModelScope.launch {
            val userId = dataStoreManager.getPreference(PreferenceKeys.USER_ID, "0").first().toIntOrNull()
            currentUserId = userId

            getEventDetailsUseCase(eventId).collect { resource ->
                when (resource) {
                    is Resource.Loader -> {
                        updateState { EventDetailsState.IsLoading(resource.isLoading) }
                    }
                    is Resource.Success -> {
                        updateState {
                            EventDetailsState.Success(
                                eventDetails = resource.data,
                                registrationId = null,
                                isRegistering = false
                            )
                        }
                    }
                    is Resource.Error -> {
                        updateState { EventDetailsState.Error(EventDetailsError.UnknownError) }
                        emitSideEffect(EventDetailsSideEffect.ShowError(EventDetailsError.UnknownError))
                    }
                }
            }
        }
    }

    private fun registerForEvent() {
        val eventId = currentEventId ?: return
        val userId = currentUserId ?: run {
            emitSideEffect(EventDetailsSideEffect.ShowToastResource(R.string.user_not_logged_in))
            return
        }

        val currentState = state.value
        if (currentState !is EventDetailsState.Success) return
        
        updateState {
            (it as EventDetailsState.Success).copy(isRegistering = true)
        }

        viewModelScope.launch {
            registerForEventUseCase(eventId, userId).collect { resource ->
                when (resource) {
                    is Resource.Loader -> {
                    }
                    is Resource.Success -> {
                        emitSideEffect(EventDetailsSideEffect.ShowToastResource(R.string.registration_successful))
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

    private fun cancelRegistration() {
        val eventId = currentEventId ?: return
        val currentState = state.value
        if (currentState !is EventDetailsState.Success) return

        val registrationId = currentState.registrationId ?: run {
            emitSideEffect(EventDetailsSideEffect.ShowToastResource(R.string.no_active_registration))
            return
        }
        
        updateState {
            (it as EventDetailsState.Success).copy(isRegistering = true)
        }

        viewModelScope.launch {
            cancelRegistrationUseCase(registrationId).collect { resource ->
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
            registrationStatus == RegistrationStatus.CONFIRMED -> stringProvider.getString(R.string.registered)
            registrationStatus == RegistrationStatus.WAITLISTED -> stringProvider.getString(R.string.waitlisted)
            isFull -> stringProvider.getString(R.string.join_waitlist)
            else -> stringProvider.getString(R.string.register_now_button)
        }
    }

    fun isButtonEnabled(registrationStatus: RegistrationStatus?): Boolean {
        return registrationStatus != RegistrationStatus.CONFIRMED &&
                registrationStatus != RegistrationStatus.WAITLISTED
    }
}
