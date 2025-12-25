package com.example.eventapplication.domain.usecase.event

import com.example.eventapplication.domain.common.Resource
import com.example.eventapplication.domain.model.EventRegistrationStatus
import com.example.eventapplication.domain.repository.RegistrationsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetEventRegistrationStatusUseCase @Inject constructor(
    private val repository: RegistrationsRepository
) {
    suspend operator fun invoke(eventId: Int): Flow<Resource<EventRegistrationStatus>> {
        return repository.getEventRegistrationStatus(eventId)
    }
}
