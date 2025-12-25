package com.example.eventapplication.data.remote.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class VerifyOtpRequestDto(
    val phoneNumber: String,
    val code: String
)
