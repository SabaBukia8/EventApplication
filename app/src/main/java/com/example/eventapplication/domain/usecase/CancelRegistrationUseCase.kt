package com.example.eventapplication.domain.usecase

import com.example.eventapplication.domain.repository.EventDetailsRepository
import com.example.eventapplication.domain.common.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CancelRegistrationUseCase @Inject constructor(
    private val repository: EventDetailsRepository
) {
    operator fun invoke(registrationId: Int): Flow<Resource<Unit>> {
        return repository.cancelRegistration(registrationId)
    }
}
