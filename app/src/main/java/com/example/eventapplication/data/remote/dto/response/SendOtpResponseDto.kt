package com.example.eventapplication.data.remote.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class SendOtpResponseDto(
    val message: String,
    val expiresInSeconds: Int? = null
)
