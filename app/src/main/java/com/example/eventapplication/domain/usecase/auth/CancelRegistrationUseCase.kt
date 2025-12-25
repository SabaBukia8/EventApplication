package com.example.eventapplication.domain.usecase.auth

import com.example.eventapplication.domain.common.Resource
import com.example.eventapplication.domain.repository.EventDetailsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CancelRegistrationUseCase @Inject constructor(
    private val repository: EventDetailsRepository
) {
    operator fun invoke(eventId: Int): Flow<Resource<Unit>> {
        return repository.cancelRegistration(eventId)
    }
}