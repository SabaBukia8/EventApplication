package com.example.eventapplication.domain.model

data class Category(
    val id: Int,
    val type: EventType?,
    val eventCount: Int
)
