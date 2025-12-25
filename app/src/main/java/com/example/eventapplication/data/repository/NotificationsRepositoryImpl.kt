package com.example.eventapplication.data.repository

import android.util.Log
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

    override fun getNotifications(): Flow<Resource<List<Notification>>> =
        handleResponse.safeApiCall {
            Log.d(
                "NotificationsRepo",
                ">>> Making API call: GET /api/Notifications/my-notifications"
            )
            val response = apiService.getNotifications()
            Log.d("NotificationsRepo", "✓ API returned ${response.size} notification DTOs")
            if (response.isNotEmpty()) {
                response.take(3).forEach { dto ->
                    Log.d("NotificationsRepo", "  - ${dto.message.take(50)}... (type: ${dto.type})")
                }
            }
            val mapped = response.toDomain()
            Log.d("NotificationsRepo", "✓ Mapped to ${mapped.size} domain notifications")
            mapped
        }

    override fun markAsRead(notificationId: String): Flow<Resource<Unit>> =
        handleResponse.safeApiCall {
            apiService.markAsRead(notificationId)
        }

    override fun markAllAsRead(): Flow<Resource<Unit>> =
        handleResponse.safeApiCall {
            apiService.markAllAsRead()
        }
}
