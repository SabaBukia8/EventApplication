package com.example.eventapplication.data.repository

import com.example.eventapplication.data.common.HandleResponse
import com.example.eventapplication.data.mapper.toDomain
import com.example.eventapplication.data.remote.api.NotificationsApiService
import com.example.eventapplication.domain.common.Resource
import com.example.eventapplication.domain.model.Notification
import com.example.eventapplication.domain.repository.NotificationsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NotificationsRepositoryImpl @Inject constructor(
    private val apiService: NotificationsApiService,
    private val handleResponse: HandleResponse
) : NotificationsRepository {

    override fun getNotifications(userId: String): Flow<Resource<List<Notification>>> =
        handleResponse.safeApiCall {
            apiService.getNotifications(userId).toDomain()
        }

    override fun markAsRead(notificationId: String): Flow<Resource<Unit>> =
        handleResponse.safeApiCall {
            apiService.markAsRead(notificationId)
        }

    override fun markAllAsRead(userId: String): Flow<Resource<Unit>> =
        handleResponse.safeApiCall {
            apiService.markAllAsRead(userId)
        }
}
