package com.example.eventapplication.data.mapper

import com.example.eventapplication.data.remote.dto.response.RegistrationEventDetailsDto
import com.example.eventapplication.data.remote.dto.response.UserEventRegistrationDto
import com.example.eventapplication.domain.model.RegisteredEvent
import com.example.eventapplication.domain.model.RegistrationStatus
import com.example.eventapplication.domain.model.UserEventRegistration

fun UserEventRegistrationDto.toDomain(): UserEventRegistration? {
    val registrationStatus = status.toRegistrationStatus()

    android.util.Log.d(
        "UserRegistrationMapper",
        "Mapping registration $registrationId: status='$status' → $registrationStatus"
    )

    if (registrationStatus == null) {
        android.util.Log.w(
            "UserRegistrationMapper",
            "Skipping registration $registrationId - invalid status: '$status'"
        )
        return null
    }

    if (registrationStatus == RegistrationStatus.CANCELLED) {
        android.util.Log.d(
            "UserRegistrationMapper",
            "Skipping registration $registrationId - status is CANCELLED"
        )
        return null
    }

    val event = if (eventTitle != null && eventType != null && startDateTime != null) {
        RegisteredEvent(
            id = eventId,
            title = eventTitle,
            description = description ?: "",
            eventType = eventType.toEventType(),
            startDateTime = startDateTime,
            endDateTime = endDateTime ?: "",
            location = buildLocation(venueName, address, onlineAddress),
            imageUrl = imageUrl,
            organizerName = organizerName ?: ""
        )
    } else {
        eventDetails?.toDomain()
    }

    return UserEventRegistration(
        registrationId = registrationId,
        eventId = eventId,
        userId = userId,
        status = registrationStatus,
        registeredAt = registeredAt,
        event = event
    )
}

private fun buildLocation(venueName: String?, address: String?, onlineAddress: String?): String {
    return when {
        onlineAddress != null -> onlineAddress
        venueName != null && address != null -> "$venueName, $address"
        venueName != null -> venueName
        address != null -> address
        else -> ""
    }
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
    return mapNotNull { it.toDomain() }
}
