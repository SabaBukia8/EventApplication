package com.example.eventapplication.domain.usecase.auth

import com.example.eventapplication.domain.common.Resource
import com.example.eventapplication.domain.repository.EventDetailsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RegisterForEventUseCase @Inject constructor(
    private val repository: EventDetailsRepository
) {
    operator fun invoke(eventId: Int, userId: Int): Flow<Resource<Int>> {
        return repository.registerForEvent(eventId, userId)
    }
}