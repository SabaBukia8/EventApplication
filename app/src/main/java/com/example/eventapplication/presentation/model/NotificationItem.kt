package com.example.eventapplication.presentation.model

import com.example.eventapplication.R
import com.example.eventapplication.domain.model.Notification
import com.example.eventapplication.domain.model.NotificationType
import com.example.eventapplication.presentation.util.toTimeAgo

sealed class NotificationItem {
    data class Header(
        val dateCategory: DateCategory,
        val displayText: String
    ) : NotificationItem()

    data class NotificationCard(
        val notification: Notification,
        val iconRes: Int,
        val timeAgo: String
    ) : NotificationItem()

    data class EmptyState(
        val message: String
    ) : NotificationItem()
}

enum class DateCategory {
    TODAY, YESTERDAY, EARLIER
}

fun Notification.toNotificationCard(): NotificationItem.NotificationCard =
    NotificationItem.NotificationCard(
        notification = this,
        iconRes = type.getIconResource(),
        timeAgo = createdAt.toTimeAgo()
    )

fun NotificationType.getIconResource(): Int = when (this) {
    NotificationType.REGISTRATION_CONFIRMATION -> R.drawable.ic_calendar
    NotificationType.EVENT_REMINDER -> R.drawable.ic_notification
    NotificationType.ORGANIZER_UPDATE -> R.drawable.ic_updates
    NotificationType.WAITLIST_PROMOTION -> R.drawable.ic_arrow_right
}
