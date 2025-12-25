package com.example.eventapplication.data.remote.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EventRegistrationStatusDto(
    val eventId: Int? = null,
    val isRegistered: Boolean? = null,
    val isWaitlisted: Boolean? = null,
    @SerialName("status")
    val registrationStatus: String? = null
)
