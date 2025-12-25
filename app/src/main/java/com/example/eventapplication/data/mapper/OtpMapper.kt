package com.example.eventapplication.data.mapper

import com.example.eventapplication.data.remote.dto.response.SendOtpResponseDto
import com.example.eventapplication.data.remote.dto.response.VerifyOtpResponseDto
import com.example.eventapplication.domain.model.SendOtpResult
import com.example.eventapplication.domain.model.VerifyOtpResult

fun SendOtpResponseDto.toDomain(): SendOtpResult {
    return SendOtpResult(
        message = message,
        expirySeconds = expiresInSeconds ?: 120
    )
}

fun VerifyOtpResponseDto.toDomain(): VerifyOtpResult {
    return VerifyOtpResult(
        message = message,
        isVerified = isVerified
    )
}
