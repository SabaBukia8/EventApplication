package com.example.eventapplication.data.remote.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class EventDetailsDto(
    val id: Int,
    val title: String,
    val description: String,
    val eventTypeName: String,
    val startDateTime: String,
    val endDateTime: String,
    val venueName: String? = null,
    val address: String? = null,
    val onlineAddress: String? = null,
    val capacity: Int,
    val confirmedCount: Int,
    val waitlistedCount: Int,
    val isFull: Boolean,
    val imageUrl: String? = null,
    val createdBy: String,
    val tags: List<String> = emptyList(),
    val registrationDeadline: String? = null
)

@Serializable
data class OrganizerDto(
    val id: Int,
    val name: String,
    val email: String,
    val avatarUrl: String? = null
)

@Serializable
data class AgendaItemDto(
    val id: Int,
    val step: Int,
    val title: String,
    val time: String,
    val description: String
)

@Serializable
data class SpeakerDto(
    val id: Int,
    val name: String,
    val title: String,
    val avatarUrl: String? = null
)

@Serializable
data class UserRegistrationDto(
    val registrationId: Int,
    val status: String
)

@Serializable
data class RegistrationResponseDto(
    val registrationId: Int? = null,
    val eventId: Int? = null,
    val userId: Int? = null,
    val status: String? = null,
    val registeredAt: String? = null
)
