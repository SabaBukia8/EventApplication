package com.example.eventapplication.data.mapper

import com.example.eventapplication.data.remote.dto.response.EventTypeDto
import com.example.eventapplication.domain.model.Category

fun EventTypeDto.toDomain(): Category {
    return Category(
        id = id,
        type = name.toEventType(),
        eventCount = eventCount
    )
}

fun List<EventTypeDto>.toDomain(): List<Category> {
    return map { it.toDomain() }
}