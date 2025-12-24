package com.example.eventapplication.data.mapper

import com.example.eventapplication.data.remote.dto.response.AuthResponseDto
import com.example.eventapplication.domain.model.AuthResult

fun AuthResponseDto.toDomain(): AuthResult {
    return AuthResult(
        token = token,
        userId = userId,
        fullName = fullName,
        role = role
    )
}
