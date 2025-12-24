package com.example.eventapplication.data.remote.api

import com.example.eventapplication.data.remote.dto.response.EventDto
import com.example.eventapplication.data.remote.dto.response.EventTypeDto
import com.example.eventapplication.data.remote.dto.response.PagedResultDto
import retrofit2.http.GET
import retrofit2.http.Query

interface EventsApiService {

    @GET("api/Events")
    suspend fun getEvents(
        @Query("eventTypeId") eventTypeId: Int? = null,
        @Query("location") location: String? = null,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null,
        @Query("searchKeyword") searchKeyword: String? = null,
        @Query("tagIds") tagIds: List<Int>? = null,
        @Query("onlyAvailable") onlyAvailable: Boolean? = null,
        @Query("eventStatus") eventStatus: String? = "Upcoming",
        @Query("pageNumber") pageNumber: Int? = null,
        @Query("pageSize") pageSize: Int? = null
    ): PagedResultDto<EventDto>

    @GET("api/Events/types")
    suspend fun getEventTypes(): List<EventTypeDto>

    @GET("api/Events/trending")
    suspend fun getTrendingEvents(
        @Query("count") count: Int = 5
    ): List<EventDto>
}
