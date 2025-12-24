package com.example.eventapplication.data.remote.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class EventTypeDto(
    val id: Int,
    val name: String,
    val description: String? = null,
    val iconUrl: String? = null
)
