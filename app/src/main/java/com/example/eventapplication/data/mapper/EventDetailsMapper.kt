package com.example.eventapplication.data.mapper

import com.example.eventapplication.data.remote.dto.response.AgendaItemDto
import com.example.eventapplication.data.remote.dto.response.EventDetailsDto
import com.example.eventapplication.data.remote.dto.response.OrganizerDto
import com.example.eventapplication.data.remote.dto.response.SpeakerDto
import com.example.eventapplication.domain.model.AgendaItem
import com.example.eventapplication.domain.model.EventDetails
import com.example.eventapplication.domain.model.EventType
import com.example.eventapplication.domain.model.Organizer
import com.example.eventapplication.domain.model.RegistrationStatus
import com.example.eventapplication.domain.model.Speaker

fun EventDetailsDto.toDomain(registrationStatus: RegistrationStatus? = null): EventDetails {
    val location = when {
        !onlineAddress.isNullOrBlank() -> onlineAddress
        !venueName.isNullOrBlank() && !address.isNullOrBlank() -> "$venueName, $address"
        !venueName.isNullOrBlank() -> venueName
        !address.isNullOrBlank() -> address
        else -> "Location TBA"
    }

    return EventDetails(
        id = id,
        title = title,
        description = description,
        eventType = eventTypeName.toEventType(),
        startDateTime = startDateTime,
        endDateTime = endDateTime,
        location = location,
        capacity = capacity,
        confirmedCount = confirmedCount,
        waitlistedCount = waitlistedCount,
        isFull = isFull,
        registrationStatus = registrationStatus,
        imageUrl = imageUrl,
        organizer = Organizer(
            id = 0,
            name = createdBy,
            email = "",
            avatarUrl = null
        ),
        tags = tags,
        agenda = emptyList(),
        speakers = emptyList(),
        registrationDeadline = registrationDeadline
    )
}

fun OrganizerDto.toDomain(): Organizer {
    return Organizer(
        id = id,
        name = name,
        email = email,
        avatarUrl = avatarUrl
    )
}

fun AgendaItemDto.toDomain(): AgendaItem {
    return AgendaItem(
        id = id,
        step = step,
        title = title,
        time = time,
        description = description
    )
}

fun SpeakerDto.toDomain(): Speaker {
    return Speaker(
        id = id,
        name = name,
        title = title,
        avatarUrl = avatarUrl
    )
}

fun String.toEventType(): EventType {
    return when (this.trim().replace("\n", "").uppercase()) {
        "TEAM BUILDING" -> EventType.TEAM_BUILDING
        "SPORTS" -> EventType.SPORTS
        "WORKSHOP" -> EventType.WORKSHOP
        "HAPPY FRIDAY" -> EventType.HAPPY_FRIDAY
        "CULTURAL" -> EventType.CULTURAL
        "WELLNESS" -> EventType.WELLNESS
        "TRAINING" -> EventType.TRAINING
        "SOCIAL" -> EventType.SOCIAL
        "CONFERENCE" -> EventType.CONFERENCE
        else -> EventType.OTHER
    }
}

fun String.toRegistrationStatus(): RegistrationStatus? {
    return when (this.uppercase()) {
        "CONFIRMED" -> RegistrationStatus.CONFIRMED
        "WAITLISTED" -> RegistrationStatus.WAITLISTED
        "CANCELLED" -> RegistrationStatus.CANCELLED
        else -> null
    }
}


