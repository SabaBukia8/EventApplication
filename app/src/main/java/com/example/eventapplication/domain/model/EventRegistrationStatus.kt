package com.example.eventapplication.domain.model

data class EventRegistrationStatus(
    val eventId: Int,
    val isRegistered: Boolean,
    val isWaitlisted: Boolean,
    val status: RegistrationStatus?
)
