package com.example.eventapplication.data.remote.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class SendOtpRequestDto(
    val phoneNumber: String
)
