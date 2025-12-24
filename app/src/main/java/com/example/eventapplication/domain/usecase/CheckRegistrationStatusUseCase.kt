package com.example.eventapplication.domain.usecase

import com.example.eventapplication.domain.model.RegistrationStatus
import com.example.eventapplication.domain.repository.EventDetailsRepository
import com.example.eventapplication.domain.common.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CheckRegistrationStatusUseCase @Inject constructor(
    private val repository: EventDetailsRepository
) {
    operator fun invoke(eventId: Int, userId: Int): Flow<Resource<RegistrationStatus?>> {
        return repository.checkRegistrationStatus(eventId, userId)
    }
}
