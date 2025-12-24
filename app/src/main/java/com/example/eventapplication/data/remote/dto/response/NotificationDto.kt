package com.example.eventapplication.data.remote.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class NotificationDto(
    val id: String,
    val userId: String,
    val title: String,
    val message: String,
    val type: String,
    val eventId: String? = null,
    val isRead: Boolean,
    val createdAt: String,
    val relatedEntityId: String? = null
)
