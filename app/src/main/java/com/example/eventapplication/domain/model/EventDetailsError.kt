package com.example.eventapplication.domain.model

// Pure Kotlin sealed class for errors - no string messages
sealed class EventDetailsError {
    object NetworkError : EventDetailsError()
    object UnauthorizedError : EventDetailsError()
    object EventNotFound : EventDetailsError()
    object EventFull : EventDetailsError()
    object AlreadyRegistered : EventDetailsError()
    object RegistrationNotFound : EventDetailsError()
    data class ServerError(val code: Int) : EventDetailsError()
    object UnknownError : EventDetailsError()
}
