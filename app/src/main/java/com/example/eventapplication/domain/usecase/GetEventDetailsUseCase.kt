package com.example.eventapplication.domain.usecase

import com.example.eventapplication.domain.model.EventDetails
import com.example.eventapplication.domain.repository.EventDetailsRepository
import com.example.eventapplication.domain.common.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetEventDetailsUseCase @Inject constructor(
    private val repository: EventDetailsRepository
) {
    operator fun invoke(eventId: Int): Flow<Resource<EventDetails>> {
        return repository.getEventDetails(eventId)
    }
}
