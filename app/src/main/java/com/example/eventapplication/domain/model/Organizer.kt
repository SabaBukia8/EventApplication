package com.example.eventapplication.domain.model

data class Organizer(
    val id: Int,
    val name: String,
    val email: String,
    val avatarUrl: String? = null
)
