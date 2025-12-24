package com.example.eventapplication.data.mapper

import com.example.eventapplication.data.remote.dto.response.AgendaItemDto
import com.example.eventapplication.data.remote.dto.response.EventDetailsDto
import com.example.eventapplication.data.remote.dto.response.OrganizerDto
import com.example.eventapplication.data.remote.dto.response.SpeakerDto
import com.example.eventapplication.data.remote.dto.response.UserRegistrationDto
import com.example.eventapplication.domain.model.AgendaItem
import com.example.eventapplication.domain.model.EventDetails
import com.example.eventapplication.domain.model.EventType
import com.example.eventapplication.domain.model.Organizer
import com.example.eventapplication.domain.model.RegistrationStatus
import com.example.eventapplication.domain.model.Speaker

fun EventDetailsDto.toDomain(registrationStatus: RegistrationStatus? = null): EventDetails {
    return EventDetails(
        id = id,
        title = title,
        description = description,
        eventType = eventType.toEventType(),
        startDateTime = startDateTime,
        endDateTime = endDateTime,
        location = location,
        capacity = capacity,
        confirmedCount = confirmedCount,
        waitlistedCount = waitlistedCount,
        isFull = isFull,
        registrationStatus = registrationStatus,
        imageUrl = imageUrl,
        organizer = organizer.toDomain(),
        tags = tags,
        agenda = agenda.map { it.toDomain() },
        speakers = speakers.map { it.toDomain() },
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

fun UserRegistrationDto.toRegistrationStatus(): RegistrationStatus? {
    return status.toRegistrationStatus()
}
