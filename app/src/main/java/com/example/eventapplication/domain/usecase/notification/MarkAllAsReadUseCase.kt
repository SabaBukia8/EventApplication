package com.example.eventapplication.domain.usecase.notification

import com.example.eventapplication.domain.common.Resource
import com.example.eventapplication.domain.repository.NotificationsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MarkAllAsReadUseCase @Inject constructor(
    private val repository: NotificationsRepository
) {
    operator fun invoke(): Flow<Resource<Unit>> =
        repository.markAllAsRead()
}
