package com.example.eventapplication.data.mapper

import com.example.eventapplication.data.remote.dto.response.UserResponseDto
import com.example.eventapplication.domain.model.User

fun UserResponseDto.toDomain(): User {
    return User(
        id = id,
        email = email,
        fullName = fullName,
        role = role
    )
}
