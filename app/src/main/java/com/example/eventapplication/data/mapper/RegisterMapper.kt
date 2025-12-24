package com.example.eventapplication.data.mapper

import com.example.eventapplication.data.remote.dto.response.RegisterResponseDto
import com.example.eventapplication.domain.model.RegisterResult

fun RegisterResponseDto.toDomain(): RegisterResult {
    return RegisterResult(message = message)
}
