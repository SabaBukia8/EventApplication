package com.example.eventapplication.domain.model

data class UserEventRegistration(
    val registrationId: Int,
    val eventId: Int,
    val userId: Int?,
    val status: RegistrationStatus,
    val registeredAt: String,
    val event: RegisteredEvent?
)

data class RegisteredEvent(
    val id: Int,
    val title: String,
    val description: String,
    val eventType: EventType,
    val startDateTime: String,
    val endDateTime: String,
    val location: String,
    val imageUrl: String?,
    val organizerName: String
)
