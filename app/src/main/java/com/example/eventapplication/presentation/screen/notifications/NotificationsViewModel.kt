package com.example.eventapplication.presentation.screen.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventapplication.domain.common.Resource
import com.example.eventapplication.domain.model.Notification
import com.example.eventapplication.domain.model.NotificationTabCategory
import com.example.eventapplication.domain.model.NotificationsError
import com.example.eventapplication.domain.usecase.notification.GetNotificationsUseCase
import com.example.eventapplication.domain.usecase.notification.MarkNotificationAsReadUseCase
import com.example.eventapplication.presentation.model.DateCategory
import com.example.eventapplication.presentation.model.NotificationItem
import com.example.eventapplication.presentation.model.toNotificationCard
import com.example.eventapplication.presentation.util.getDateCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val getNotificationsUseCase: GetNotificationsUseCase,
    private val markNotificationAsReadUseCase: MarkNotificationAsReadUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<NotificationsState>(NotificationsState.Idle)
    val state: StateFlow<NotificationsState> = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<NotificationsSideEffect>()
    val sideEffect: SharedFlow<NotificationsSideEffect> = _sideEffect.asSharedFlow()

    private var allNotifications: List<Notification> = emptyList()
    private var currentTab: NotificationTabCategory = NotificationTabCategory.ALL

    init {
        onEvent(NotificationsEvent.LoadNotifications)
    }

    fun onEvent(event: NotificationsEvent) {
        when (event) {
            NotificationsEvent.LoadNotifications -> loadNotifications()
            NotificationsEvent.RefreshNotifications -> refreshNotifications()
            is NotificationsEvent.TabSelected -> filterByTab(event.tab)
            is NotificationsEvent.NotificationClicked -> handleNotificationClick(event.notification)
            is NotificationsEvent.MarkAsRead -> markAsRead(event.notificationId)
            NotificationsEvent.MarkAllAsRead -> markAllAsRead()
        }
    }

    private fun loadNotifications() {
        viewModelScope.launch {
            getNotificationsUseCase().collect { resource ->
                when (resource) {
                    is Resource.Loader -> _state.value = NotificationsState.IsLoading(resource.isLoading)
                    is Resource.Success -> {
                        allNotifications = resource.data
                        updateStateWithFiltered()
                    }
                    is Resource.Error -> _state.value = NotificationsState.Error(resource.error as NotificationsError)
                }
            }
        }
    }

    private fun refreshNotifications() {
        loadNotifications()
    }

    private fun filterByTab(tab: NotificationTabCategory) {
        currentTab = tab
        updateStateWithFiltered()
    }

    private fun updateStateWithFiltered() {
        val filtered = when (currentTab) {
            NotificationTabCategory.ALL -> allNotifications
            else -> allNotifications.filter { it.type.getTabCategory() == currentTab }
        }

        val grouped = groupNotificationsByDate(filtered)
        val unreadCount = allNotifications.count { !it.isRead }

        _state.value = NotificationsState.Success(
            notifications = grouped,
            unreadCount = unreadCount,
            selectedTab = currentTab
        )
    }

    private fun groupNotificationsByDate(notifications: List<Notification>): List<NotificationItem> {
        if (notifications.isEmpty()) {
            return listOf(NotificationItem.EmptyState("No notifications yet"))
        }

        val today = mutableListOf<Notification>()
        val yesterday = mutableListOf<Notification>()
        val earlier = mutableListOf<Notification>()

        notifications.forEach { notification ->
            when (notification.createdAt.getDateCategory()) {
                DateCategory.TODAY -> today.add(notification)
                DateCategory.YESTERDAY -> yesterday.add(notification)
                DateCategory.EARLIER -> earlier.add(notification)
            }
        }

        return buildList {
            if (today.isNotEmpty()) {
                add(NotificationItem.Header(DateCategory.TODAY, "Today"))
                addAll(today.map { it.toNotificationCard() })
            }
            if (yesterday.isNotEmpty()) {
                add(NotificationItem.Header(DateCategory.YESTERDAY, "Yesterday"))
                addAll(yesterday.map { it.toNotificationCard() })
            }
            if (earlier.isNotEmpty()) {
                add(NotificationItem.Header(DateCategory.EARLIER, "Earlier"))
                addAll(earlier.map { it.toNotificationCard() })
            }
        }
    }

    private fun handleNotificationClick(notification: Notification) {
        viewModelScope.launch {
            if (!notification.isRead) {
                markAsRead(notification.id)
            }
            _sideEffect.emit(NotificationsSideEffect.ShowNotificationDetail(notification))
        }
    }

    private fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            markNotificationAsReadUseCase(notificationId).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        allNotifications = allNotifications.map {
                            if (it.id == notificationId) it.copy(isRead = true) else it
                        }
                        updateStateWithFiltered()
                    }
                    is Resource.Error -> {
                        _sideEffect.emit(NotificationsSideEffect.ShowError("Failed to mark as read"))
                    }
                    else -> {}
                }
            }
        }
    }

    private fun markAllAsRead() {
        // Will be implemented when needed
    }
}
