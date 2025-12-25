package com.example.eventapplication.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Notification(
    val id: String,
    val userId: String,
    val title: String,
    val message: String,
    val type: NotificationType,
    val eventId: String?,
    val isRead: Boolean,
    val createdAt: String,
    val relatedEntityId: String?
) : Parcelable

enum class NotificationType {
    REGISTRATION_CONFIRMATION,
    EVENT_REMINDER,
    ORGANIZER_UPDATE,
    WAITLIST_PROMOTION;

    fun getDisplayName(): String = when (this) {
        REGISTRATION_CONFIRMATION -> "Registration"
        EVENT_REMINDER -> "Alert"
        ORGANIZER_UPDATE -> "Update"
        WAITLIST_PROMOTION -> "Alert"
    }

    fun getTabCategory(): NotificationTabCategory = when (this) {
        REGISTRATION_CONFIRMATION -> NotificationTabCategory.REGISTRATIONS
        EVENT_REMINDER -> NotificationTabCategory.ALERTS
        ORGANIZER_UPDATE -> NotificationTabCategory.UPDATES
        WAITLIST_PROMOTION -> NotificationTabCategory.ALERTS
    }
}

enum class NotificationTabCategory {
    ALL, REGISTRATIONS, UPDATES, ALERTS
}
