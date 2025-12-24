package com.example.eventapplication.domain.usecase

import com.example.eventapplication.domain.common.Resource
import com.example.eventapplication.domain.repository.NotificationsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MarkNotificationAsReadUseCase @Inject constructor(
    private val repository: NotificationsRepository
) {
    operator fun invoke(notificationId: String): Flow<Resource<Unit>> =
        repository.markAsRead(notificationId)
}
