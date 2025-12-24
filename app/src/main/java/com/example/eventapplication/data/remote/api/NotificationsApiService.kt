package com.example.eventapplication.data.remote.api

import com.example.eventapplication.data.remote.dto.response.NotificationDto
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface NotificationsApiService {

    @GET("api/Notifications/my-notifications")
    suspend fun getNotifications(): List<NotificationDto>

    @PUT("api/Notifications/{notificationId}/read")
    suspend fun markAsRead(@Path("notificationId") notificationId: String)

    @PUT("api/Notifications/user/{userId}/read-all")
    suspend fun markAllAsRead(@Path("userId") userId: String)
}
