package com.example.eventapplication.data.mapper

import com.example.eventapplication.data.remote.dto.response.NotificationDto
import com.example.eventapplication.domain.model.Notification
import com.example.eventapplication.domain.model.NotificationType

fun NotificationDto.toDomain(): Notification = Notification(
    id = id,
    userId = userId,
    title = title,
    message = message,
    type = type.toNotificationType(),
    eventId = eventId,
    isRead = isRead,
    createdAt = createdAt,
    relatedEntityId = relatedEntityId
)

fun List<NotificationDto>.toDomain(): List<Notification> = map { it.toDomain() }

private fun String.toNotificationType(): NotificationType = when (this.lowercase().replace("_", "")) {
    "registration", "registrationconfirmation" -> NotificationType.REGISTRATION_CONFIRMATION
    "eventreminder", "reminder" -> NotificationType.EVENT_REMINDER
    "organizerupdate", "update" -> NotificationType.ORGANIZER_UPDATE
    "waitlistpromotion", "waitlist" -> NotificationType.WAITLIST_PROMOTION
    else -> NotificationType.ORGANIZER_UPDATE
}
