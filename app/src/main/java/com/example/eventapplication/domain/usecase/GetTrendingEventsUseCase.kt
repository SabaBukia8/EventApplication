package com.example.eventapplication.domain.usecase

import com.example.eventapplication.domain.common.Resource
import com.example.eventapplication.domain.model.Event
import com.example.eventapplication.domain.repository.EventsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetTrendingEventsUseCase @Inject constructor(
    private val repository: EventsRepository
) {
    operator fun invoke(limit: Int = 10): Flow<Resource<List<Event>>> {
        return repository.getEvents(
            eventStatus = "Upcoming",
            pageNumber = 1,
            pageSize = limit
        ).map { resource ->
            when (resource) {
                is Resource.Success -> {
                    val sortedEvents = resource.data.items.sortedByDescending { it.confirmedCount }
                    Resource.Success(sortedEvents)
                }
                is Resource.Error -> resource
                is Resource.Loader -> resource
            }
        }
    }
}
