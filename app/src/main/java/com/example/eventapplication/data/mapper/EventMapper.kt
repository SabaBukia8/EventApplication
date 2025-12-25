package com.example.eventapplication.data.mapper

import com.example.eventapplication.data.remote.dto.response.EventDto
import com.example.eventapplication.domain.model.Event
import com.example.eventapplication.domain.model.Organizer

fun EventDto.toDomain(): Event {
    val locationString = when {
        !venueName.isNullOrEmpty() && !address.isNullOrEmpty() -> "$venueName, $address"
        !venueName.isNullOrEmpty() -> venueName
        !address.isNullOrEmpty() -> address
        !onlineAddress.isNullOrEmpty() -> onlineAddress
        else -> "TBA"
    }

    return Event(
        id = id,
        title = title,
        description = description ?: "",
        eventType = eventTypeName.toEventType(),
        startDateTime = startDateTime,
        endDateTime = endDateTime ?: "",
        location = locationString,
        capacity = capacity,
        confirmedCount = confirmedCount,
        isFull = isFull,
        isWaitlisted = (waitlistedCount ?: 0) > 0,
        registrationStatus = null,
        imageUrl = imageUrl,
        organizer = Organizer(
            id = organizerId ?: 0,
            name = organizerName ?: "",
            email = "",
            avatarUrl = null
        )
    )
}

fun List<EventDto>.toDomain(): List<Event> {
    return map { it.toDomain() }
}
