package com.example.eventapplication.domain.usecase.event

import com.example.eventapplication.domain.common.Resource
import com.example.eventapplication.domain.model.Event
import com.example.eventapplication.domain.model.SortOption
import com.example.eventapplication.domain.repository.EventsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetEventsByCategoryUseCase @Inject constructor(
    private val repository: EventsRepository
) {
    operator fun invoke(
        categoryId: Int,
        sortBy: SortOption = SortOption.START_DATE,
        pageNumber: Int = 1,
        pageSize: Int = 50
    ): Flow<Resource<List<Event>>> {
        return repository.getEvents(
            eventTypeId = categoryId,
            eventStatus = "Upcoming",
            pageNumber = pageNumber,
            pageSize = pageSize
        ).map { resource ->
            when (resource) {
                is Resource.Success -> {
                    val sortedEvents = when (sortBy) {
                        SortOption.START_DATE -> resource.data.items.sortedBy { it.startDateTime }
                        SortOption.POPULARITY -> resource.data.items.sortedByDescending { it.confirmedCount }
                        SortOption.SPOTS_AVAILABLE -> resource.data.items.sortedByDescending { it.spotsLeft }
                    }
                    Resource.Success(sortedEvents)
                }
                is Resource.Error -> resource
                is Resource.Loader -> resource
            }
        }
    }
}