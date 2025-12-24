package com.example.eventapplication.domain.usecase.event

import com.example.eventapplication.domain.common.Resource
import com.example.eventapplication.domain.model.Event
import com.example.eventapplication.domain.repository.EventsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTrendingEventsUseCase @Inject constructor(
    private val repository: EventsRepository
) {
    operator fun invoke(limit: Int = 10): Flow<Resource<List<Event>>> {
        return repository.getTrendingEvents(count = limit)
    }
}