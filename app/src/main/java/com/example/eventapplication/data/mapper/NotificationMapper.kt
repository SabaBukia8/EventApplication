package com.example.eventapplication.data.mapper

import com.example.eventapplication.data.remote.dto.response.NotificationDto
import com.example.eventapplication.domain.model.Notification
import com.example.eventapplication.domain.model.NotificationType

fun NotificationDto.toDomain(): Notification = Notification(
    id = id.toString(),
    userId = "",
    title = type.toNotificationTitle(),
    message = message,
    type = type.toNotificationType(),
    eventId = eventId?.toString(),
    isRead = readAt != null,
    createdAt = sentAt,
    relatedEntityId = eventId?.toString()
)

fun List<NotificationDto>.toDomain(): List<Notification> = map { it.toDomain() }

private fun String.toNotificationType(): NotificationType =
    when (this.lowercase().replace("_", "")) {
        "registration", "registrationconfirmation" -> NotificationType.REGISTRATION_CONFIRMATION
        "cancellation", "cancellationconfirmation" -> NotificationType.REGISTRATION_CONFIRMATION
        "eventreminder", "reminder" -> NotificationType.EVENT_REMINDER
        "organizerupdate", "update" -> NotificationType.ORGANIZER_UPDATE
        "waitlistpromotion", "waitlist" -> NotificationType.WAITLIST_PROMOTION
        else -> NotificationType.ORGANIZER_UPDATE
    }

private fun String.toNotificationTitle(): String = when (this.lowercase().replace("_", "")) {
    "registration", "registrationconfirmation" -> "Registration Confirmed"
    "cancellation", "cancellationconfirmation" -> "Registration Cancelled"
    "eventreminder", "reminder" -> "Event Reminder"
    "organizerupdate", "update" -> "Event Update"
    "waitlistpromotion", "waitlist" -> "Waitlist Update"
    else -> "Notification"
}
