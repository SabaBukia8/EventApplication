package com.example.eventapplication.data.remote.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class RegisterEventRequestDto(
    val eventId: Int,
    val userId: Int
)
