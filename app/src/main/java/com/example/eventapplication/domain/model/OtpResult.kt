package com.example.eventapplication.domain.model

data class SendOtpResult(
    val message: String,
    val expirySeconds: Int = 120
)

data class VerifyOtpResult(
    val message: String,
    val isVerified: Boolean
)
