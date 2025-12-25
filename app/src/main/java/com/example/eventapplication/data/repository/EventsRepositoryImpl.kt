package com.example.eventapplication.data.repository

import com.example.eventapplication.data.common.HandleResponse
import com.example.eventapplication.data.mapper.toDomain
import com.example.eventapplication.data.remote.api.EventsApiService
import com.example.eventapplication.domain.common.Resource
import com.example.eventapplication.domain.model.Category
import com.example.eventapplication.domain.model.Event
import com.example.eventapplication.domain.model.PaginatedResult
import com.example.eventapplication.domain.repository.EventsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class EventsRepositoryImpl @Inject constructor(
    private val apiService: EventsApiService,
    private val handleResponse: HandleResponse
) : EventsRepository {

    override fun getEvents(
        eventTypeId: Int?,
        location: String?,
        startDate: String?,
        endDate: String?,
        searchKeyword: String?,
        tagIds: List<Int>?,
        onlyAvailable: Boolean?,
        eventStatus: String?,
        pageNumber: Int?,
        pageSize: Int?
    ): Flow<Resource<PaginatedResult<Event>>> {
        return handleResponse.safeApiCall {
            apiService.getEvents(
                eventTypeId = eventTypeId,
                location = location,
                startDate = startDate,
                endDate = endDate,
                searchKeyword = searchKeyword,
                tagIds = tagIds,
                onlyAvailable = onlyAvailable,
                eventStatus = eventStatus,
                pageNumber = pageNumber,
                pageSize = pageSize
            )
        }.map { resource ->
            when (resource) {
                is Resource.Success -> Resource.Success(
                    resource.data.toDomain { eventDto -> eventDto.toDomain() }
                )

                is Resource.Error -> resource
                is Resource.Loader -> resource
            }
        }
    }

    override fun getEventTypes(): Flow<Resource<List<Category>>> {
        return handleResponse.safeApiCall {
            apiService.getEventTypes().toDomain()
        }
    }

    override fun getTrendingEvents(count: Int): Flow<Resource<List<Event>>> {
        return handleResponse.safeApiCall {
            apiService.getTrendingEvents(count)
        }.map { resource ->
            when (resource) {
                is Resource.Success -> Resource.Success(
                    resource.data.map { it.toDomain() }
                )

                is Resource.Error -> resource
                is Resource.Loader -> resource
            }
        }
    }
}
