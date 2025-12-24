package com.example.eventapplication.data.remote.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class UserEventRegistrationDto(
    val registrationId: Int,
    val eventId: Int,
    val userId: Int,
    val status: String,
    val registeredAt: String,
    val eventDetails: RegistrationEventDetailsDto
)

@Serializable
data class RegistrationEventDetailsDto(
    val id: Int,
    val title: String,
    val description: String,
    val eventType: String,
    val startDateTime: String,
    val endDateTime: String,
    val location: String,
    val imageUrl: String? = null,
    val organizerName: String
)
