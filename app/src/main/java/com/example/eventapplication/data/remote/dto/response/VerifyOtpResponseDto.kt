package com.example.eventapplication.data.remote.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class VerifyOtpResponseDto(
    val message: String,
    val isVerified: Boolean
)
