package com.example.eventapplication.data.mapper

import com.example.eventapplication.data.remote.dto.response.RegistrationEventDetailsDto
import com.example.eventapplication.data.remote.dto.response.UserEventRegistrationDto
import com.example.eventapplication.domain.model.RegisteredEvent
import com.example.eventapplication.domain.model.UserEventRegistration

fun UserEventRegistrationDto.toDomain(): UserEventRegistration {
    return UserEventRegistration(
        registrationId = registrationId,
        eventId = eventId,
        userId = userId,
        status = status.toRegistrationStatus() ?: throw IllegalArgumentException("Invalid registration status: $status"),
        registeredAt = registeredAt,
        event = eventDetails.toDomain()
    )
}

fun RegistrationEventDetailsDto.toDomain(): RegisteredEvent {
    return RegisteredEvent(
        id = id,
        title = title,
        description = description,
        eventType = eventType.toEventType(),
        startDateTime = startDateTime,
        endDateTime = endDateTime,
        location = location,
        imageUrl = imageUrl,
        organizerName = organizerName
    )
}

fun List<UserEventRegistrationDto>.toDomain(): List<UserEventRegistration> {
    return map { it.toDomain() }
}
