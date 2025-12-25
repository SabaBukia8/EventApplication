package com.example.eventapplication.presentation.screen.myevents

import androidx.lifecycle.viewModelScope
import com.example.eventapplication.domain.common.Resource
import com.example.eventapplication.domain.model.NetworkError
import com.example.eventapplication.domain.model.RegistrationStatus
import com.example.eventapplication.domain.model.UserEventRegistration
import com.example.eventapplication.domain.usecase.event.GetUserRegistrationsUseCase
import com.example.eventapplication.presentation.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MyEventsViewModel @Inject constructor(
    private val getUserRegistrationsUseCase: GetUserRegistrationsUseCase
) : BaseViewModel<MyEventsState, MyEventsEvent, MyEventsSideEffect>(
    initialState = MyEventsState.Idle
) {

    override fun onEvent(event: MyEventsEvent) {
        when (event) {
            is MyEventsEvent.LoadRegistrations -> loadRegistrations()
            is MyEventsEvent.OnRefresh -> handleRefresh()
            is MyEventsEvent.OnEventClicked -> navigateToEventDetails(event.eventId)
            is MyEventsEvent.OnCalendarViewClicked -> showCalendarComingSoon()
            is MyEventsEvent.OnRetry -> loadRegistrations()
        }
    }

    private fun loadRegistrations() {
        updateState { MyEventsState.IsLoading(true) }

        viewModelScope.launch {
            try {
                val result = getUserRegistrationsUseCase().first {
                    it is Resource.Success || it is Resource.Error
                }

                when (result) {
                    is Resource.Success -> {
                        val activeRegistrations = result.data
                            .filter {
                                it.status == RegistrationStatus.CONFIRMED ||
                                it.status == RegistrationStatus.WAITLISTED
                            }
                            .sortedBy { parseDateTime(it.event.startDateTime) }

                        if (activeRegistrations.isEmpty()) {
                            updateState { MyEventsState.Empty }
                        } else {
                            val nextUpcomingId = identifyNextUpcomingEvent(activeRegistrations)
                            updateState {
                                MyEventsState.Success(
                                    registrations = activeRegistrations,
                                    nextUpcomingEventId = nextUpcomingId
                                )
                            }
                        }
                    }
                    is Resource.Error -> {
                        val errorMessage = getErrorMessage(result.error)
                        updateState { MyEventsState.Error(errorMessage) }
                    }
                    is Resource.Loader -> {
                        updateState { MyEventsState.IsLoading(result.isLoading) }
                    }
                }
            } catch (e: Exception) {
                updateState { MyEventsState.Error(e.message ?: "Unknown error occurred") }
            }
        }
    }

    private fun handleRefresh() {
        loadRegistrations()
    }

    private fun navigateToEventDetails(eventId: Int) {
        emitSideEffect(MyEventsSideEffect.NavigateToEventDetails(eventId))
    }

    private fun showCalendarComingSoon() {
        emitSideEffect(MyEventsSideEffect.ShowMessage("Calendar view coming soon!"))
    }

    private fun identifyNextUpcomingEvent(registrations: List<UserEventRegistration>): Int? {
        val now = System.currentTimeMillis()

        return registrations
            .filter {
                it.status == RegistrationStatus.CONFIRMED ||
                it.status == RegistrationStatus.WAITLISTED
            }
            .firstOrNull { registration ->
                val eventTime = parseDateTime(registration.event.startDateTime)
                eventTime > now
            }
            ?.eventId
    }

    private fun parseDateTime(dateTimeString: String): Long {
        return try {
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            format.parse(dateTimeString.replace("Z", ""))?.time ?: 0L
        } catch (e: Exception) {
            0L
        }
    }
    
    private fun getErrorMessage(error: NetworkError): String {
        return when (error) {
            is NetworkError.Unknown -> error.message ?: "Unknown error occurred"
            is NetworkError.NoInternet -> "No internet connection"
            is NetworkError.Unauthorized -> "Authentication required"
            is NetworkError.Forbidden -> "Access denied"
            is NetworkError.NotFound -> "Resource not found"
            is NetworkError.ServerError -> "Server error occurred"
            is NetworkError.Timeout -> "Connection timed out"
            else -> "Unable to load your events"
        }
    }
}
