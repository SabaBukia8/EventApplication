package com.example.eventapplication.domain.model

data class AuthResult(
    val token: String,
    val userId: Int,
    val fullName: String,
    val role: String
)
