package com.example.eventapplication.data.remote.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class EventDto(
    val id: Int,
    val title: String,
    val description: String? = "",
    val eventTypeId: Int? = 0,
    val eventTypeName: String,
    val startDateTime: String,
    val endDateTime: String? = "",
    val venueName: String? = null,
    val address: String? = null,
    val onlineAddress: String? = null,
    val capacity: Int,
    val confirmedCount: Int,
    val waitlistedCount: Int? = 0,
    val isFull: Boolean,
    val imageUrl: String? = null,
    val organizerId: Int? = 0,
    val organizerName: String? = "",
    val tags: List<String> = emptyList(),
    val registrationDeadline: String? = null,
    val eventStatus: String? = ""
)
