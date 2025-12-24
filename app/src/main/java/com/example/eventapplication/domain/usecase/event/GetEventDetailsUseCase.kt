package com.example.eventapplication.domain.usecase.event

import com.example.eventapplication.domain.common.Resource
import com.example.eventapplication.domain.model.EventDetails
import com.example.eventapplication.domain.repository.EventDetailsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetEventDetailsUseCase @Inject constructor(
    private val repository: EventDetailsRepository
) {
    operator fun invoke(eventId: Int): Flow<Resource<EventDetails>> {
        return repository.getEventDetails(eventId)
    }
}