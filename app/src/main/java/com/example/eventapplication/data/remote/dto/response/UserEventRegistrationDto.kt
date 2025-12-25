package com.example.eventapplication.data.remote.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class UserEventRegistrationDto(
    val registrationId: Int,
    val eventId: Int,
    val userId: Int? = null,
    val status: String,
    val registeredAt: String,
    // Flat fields from API response
    val eventTitle: String? = null,
    val eventType: String? = null,
    val startDateTime: String? = null,
    val endDateTime: String? = null,
    val venueName: String? = null,
    val address: String? = null,
    val onlineAddress: String? = null,
    val imageUrl: String? = null,
    val description: String? = null,
    val organizerName: String? = null,
    // Legacy nested structure (keep for backwards compatibility)
    val eventDetails: RegistrationEventDetailsDto? = null
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
