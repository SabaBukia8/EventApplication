package com.example.eventapplication.presentation.screen.notifications

import com.example.eventapplication.domain.model.Notification
import com.example.eventapplication.domain.model.NotificationTabCategory
import com.example.eventapplication.domain.model.NotificationsError
import com.example.eventapplication.presentation.model.NotificationItem

sealed class NotificationsState {
    data object Idle : NotificationsState()
    data class IsLoading(val isLoading: Boolean) : NotificationsState()
    data class Success(
        val notifications: List<NotificationItem>,
        val unreadCount: Int,
        val selectedTab: NotificationTabCategory
    ) : NotificationsState()

    data class Error(val error: NotificationsError) : NotificationsState()
}

sealed class NotificationsEvent {
    data object LoadNotifications : NotificationsEvent()
    data object RefreshNotifications : NotificationsEvent()
    data class TabSelected(val tab: NotificationTabCategory) : NotificationsEvent()
    data class NotificationClicked(val notification: Notification) : NotificationsEvent()
    data class MarkAsRead(val notificationId: String) : NotificationsEvent()
    data object MarkAllAsRead : NotificationsEvent()
}

sealed class NotificationsSideEffect {
    data class ShowNotificationDetail(val notification: Notification) : NotificationsSideEffect()
    data class ShowError(val message: String) : NotificationsSideEffect()
    data class NavigateToEvent(val eventId: String) : NotificationsSideEffect()
}
