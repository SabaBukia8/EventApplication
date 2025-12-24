package com.example.eventapplication.data.remote.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequestDto(
    val firstName: String,
    val lastName: String,
    val email: String,
    val mobileNumber: String,
    val departmentId: Int,
    val password: String,
    val confirmPassword: String
)
