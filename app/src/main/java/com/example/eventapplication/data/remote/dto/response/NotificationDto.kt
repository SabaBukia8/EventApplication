package com.example.eventapplication.data.remote.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationDto(
    val id: Int,
    val eventId: Int? = null,
    @SerialName("notificationType")
    val type: String,
    val message: String,
    val sentAt: String,
    val status: String,
    val readAt: String? = null
)
