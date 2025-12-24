package com.example.eventapplication.data.remote.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class EventDetailsDto(
    val id: Int,
    val title: String,
    val description: String,
    val eventType: String,
    val startDateTime: String,
    val endDateTime: String,
    val location: String,
    val capacity: Int,
    val confirmedCount: Int,
    val waitlistedCount: Int,
    val isFull: Boolean,
    val imageUrl: String? = null,
    val organizer: OrganizerDto,
    val tags: List<String> = emptyList(),
    val agenda: List<AgendaItemDto> = emptyList(),
    val speakers: List<SpeakerDto> = emptyList(),
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
    val registrationId: Int,
    val eventId: Int,
    val userId: Int,
    val status: String,
    val registeredAt: String
)
