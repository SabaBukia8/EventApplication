package com.example.eventapplication.data.mapper

import com.example.eventapplication.data.remote.dto.response.EventRegistrationStatusDto
import com.example.eventapplication.domain.model.EventRegistrationStatus
import com.example.eventapplication.domain.model.RegistrationStatus

fun EventRegistrationStatusDto.toDomain(): EventRegistrationStatus {
    return EventRegistrationStatus(
        eventId = eventId,
        isRegistered = isRegistered,
        isWaitlisted = isWaitlisted,
        status = registrationStatus?.let {
            when (it.uppercase()) {
                "CONFIRMED" -> RegistrationStatus.CONFIRMED
                "WAITLISTED" -> RegistrationStatus.WAITLISTED
                "CANCELLED" -> RegistrationStatus.CANCELLED
                else -> null
            }
        }
    )
}
