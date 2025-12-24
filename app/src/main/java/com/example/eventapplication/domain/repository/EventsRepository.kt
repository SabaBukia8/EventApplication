package com.example.eventapplication.domain.repository

import com.example.eventapplication.domain.common.Resource
import com.example.eventapplication.domain.model.Category
import com.example.eventapplication.domain.model.Event
import com.example.eventapplication.domain.model.PaginatedResult
import kotlinx.coroutines.flow.Flow

interface EventsRepository {

    fun getEvents(
        eventTypeId: Int? = null,
        location: String? = null,
        startDate: String? = null,
        endDate: String? = null,
        searchKeyword: String? = null,
        tagIds: List<Int>? = null,
        onlyAvailable: Boolean? = null,
        eventStatus: String? = "Upcoming",
        pageNumber: Int? = null,
        pageSize: Int? = null
    ): Flow<Resource<PaginatedResult<Event>>>

    fun getEventTypes(): Flow<Resource<List<Category>>>
}
