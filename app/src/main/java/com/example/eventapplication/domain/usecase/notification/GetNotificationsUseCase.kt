package com.example.eventapplication.domain.usecase.notification

import android.util.Log
import com.example.eventapplication.domain.common.Resource
import com.example.eventapplication.domain.model.Notification
import com.example.eventapplication.domain.model.NotificationsError
import com.example.eventapplication.domain.repository.NotificationsRepository
import com.example.eventapplication.domain.repository.TokenRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetNotificationsUseCase @Inject constructor(
    private val notificationsRepository: NotificationsRepository,
    private val tokenRepository: TokenRepository
) {
    operator fun invoke(): Flow<Resource<List<Notification>>> = flow {
        Log.d("GetNotificationsUseCase", ">>> Starting notification fetch")
        emit(Resource.Loader(isLoading = true))

        // Wait for actual Success or Error, skip Loader emissions
        val userResult = tokenRepository.getCurrentUser().first { it !is Resource.Loader }
        Log.d("GetNotificationsUseCase", "User auth result: $userResult")

        when (userResult) {
            is Resource.Success -> {
                Log.d("GetNotificationsUseCase", "User authenticated: ${userResult.data?.email}")
                notificationsRepository.getNotifications().collect { resource ->
                    Log.d("GetNotificationsUseCase", "Repository response: ${resource::class.simpleName}")
                    when (resource) {
                        is Resource.Loader -> {
                            if (!resource.isLoading) emit(Resource.Loader(isLoading = false))
                        }
                        is Resource.Success -> {
                            Log.d("GetNotificationsUseCase", "✓ Success! Notification count: ${resource.data.size}")
                            emit(resource)
                        }
                        is Resource.Error -> {
                            Log.e("GetNotificationsUseCase", "✗ Repository error: ${resource.error}")
                            emit(resource)
                        }
                        else -> emit(resource)
                    }
                }
            }

            is Resource.Error -> {
                Log.e("GetNotificationsUseCase", "✗ User auth failed: ${userResult.error}")
                emit(Resource.Error(NotificationsError.Unauthorized))
                emit(Resource.Loader(isLoading = false))
            }

            else -> {
                Log.e("GetNotificationsUseCase", "✗ Unexpected user result type")
                emit(Resource.Error(NotificationsError.Unauthorized))
                emit(Resource.Loader(isLoading = false))
            }
        }
    }
}