package com.example.eventapplication.domain.repository

import com.example.eventapplication.domain.common.Resource
import com.example.eventapplication.domain.model.Notification
import kotlinx.coroutines.flow.Flow

interface NotificationsRepository {

    fun getNotifications(): Flow<Resource<List<Notification>>>

    fun markAsRead(notificationId: String): Flow<Resource<Unit>>

    fun markAllAsRead(): Flow<Resource<Unit>>
}
