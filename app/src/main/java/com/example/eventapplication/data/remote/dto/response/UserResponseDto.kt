package com.example.eventapplication.data.remote.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class UserResponseDto(
    val id: Int,
    val email: String,
    val fullName: String,
    val role: String
)
