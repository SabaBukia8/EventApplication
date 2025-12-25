package com.example.eventapplication.data.remote.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class EventRegistrationStatusDto(
    val eventId: Int,
    val isRegistered: Boolean,
    val isWaitlisted: Boolean,
    val registrationStatus: String? = null
)
