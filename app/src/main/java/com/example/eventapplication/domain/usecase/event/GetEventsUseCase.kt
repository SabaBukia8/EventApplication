package com.example.eventapplication.domain.usecase.event

import com.example.eventapplication.domain.common.Resource
import com.example.eventapplication.domain.model.Event
import com.example.eventapplication.domain.repository.EventsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetEventsUseCase @Inject constructor(
    private val repository: EventsRepository
) {
    operator fun invoke(
        eventTypeId: Int? = null,
        location: String? = null,
        startDate: String? = null,
        endDate: String? = null,
        searchKeyword: String? = null,
        onlyAvailable: Boolean? = null,
        eventStatus: String? = "Upcoming",
        pageNumber: Int = 1,
        pageSize: Int = 50
    ): Flow<Resource<List<Event>>> {
        return repository.getEvents(
            eventTypeId = eventTypeId,
            location = location,
            startDate = startDate,
            endDate = endDate,
            searchKeyword = searchKeyword,
            onlyAvailable = onlyAvailable,
            eventStatus = eventStatus,
            pageNumber = pageNumber,
            pageSize = pageSize
        ).map { resource ->
            when (resource) {
                is Resource.Success -> Resource.Success(resource.data.items)
                is Resource.Error -> resource
                is Resource.Loader -> resource
            }
        }
    }
}