package com.example.eventapplication.data.remote.api

import com.example.eventapplication.data.remote.dto.response.EventRegistrationStatusDto
import retrofit2.http.GET
import retrofit2.http.Path

interface EventRegistrationStatusApiService {

    @GET("api/Registrations/event/{eventId}/status")
    suspend fun getEventRegistrationStatus(
        @Path("eventId") eventId: Int
    ): EventRegistrationStatusDto
}
