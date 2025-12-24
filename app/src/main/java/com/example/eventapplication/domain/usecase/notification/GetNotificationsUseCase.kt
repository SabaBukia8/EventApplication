package com.example.eventapplication.domain.usecase.notification

import com.example.eventapplication.domain.common.Resource
import com.example.eventapplication.domain.model.Notification
import com.example.eventapplication.domain.model.NotificationsError
import com.example.eventapplication.domain.repository.NotificationsRepository
import com.example.eventapplication.domain.repository.TokenRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetNotificationsUseCase @Inject constructor(
    private val notificationsRepository: NotificationsRepository,
    private val tokenRepository: TokenRepository
) {
    operator fun invoke(): Flow<Resource<List<Notification>>> = flow {
        emit(Resource.Loader(isLoading = true))

        val userResult = tokenRepository.getCurrentUser().firstOrNull()

        when (userResult) {
            is Resource.Success -> {
                notificationsRepository.getNotifications().collect { resource ->
                    when (resource) {
                        is Resource.Loader -> if (!resource.isLoading) emit(
                            Resource.Loader(
                                isLoading = false
                            )
                        )

                        else -> emit(resource)
                    }
                }
            }

            is Resource.Error -> {
                emit(Resource.Error(NotificationsError.Unauthorized))
                emit(Resource.Loader(isLoading = false))
            }

            else -> {
                emit(Resource.Error(NotificationsError.Unauthorized))
                emit(Resource.Loader(isLoading = false))
            }
        }
    }
}