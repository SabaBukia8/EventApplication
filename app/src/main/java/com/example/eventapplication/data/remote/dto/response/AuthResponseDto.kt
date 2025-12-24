package com.example.eventapplication.data.remote.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponseDto(
    val token: String,
    val userId: Int,
    val fullName: String,
    val role: String
)
