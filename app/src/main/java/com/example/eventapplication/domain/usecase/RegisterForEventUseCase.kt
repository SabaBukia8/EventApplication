package com.example.eventapplication.domain.usecase

import com.example.eventapplication.domain.repository.EventDetailsRepository
import com.example.eventapplication.domain.common.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RegisterForEventUseCase @Inject constructor(
    private val repository: EventDetailsRepository
) {
    operator fun invoke(eventId: Int, userId: Int): Flow<Resource<Int>> {
        return repository.registerForEvent(eventId, userId)
    }
}
