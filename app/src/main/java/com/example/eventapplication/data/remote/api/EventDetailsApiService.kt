package com.example.eventapplication.data.remote.api

import com.example.eventapplication.data.remote.dto.request.RegisterEventRequestDto
import com.example.eventapplication.data.remote.dto.response.EventDetailsDto
import com.example.eventapplication.data.remote.dto.response.RegistrationResponseDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface EventDetailsApiService {

    @GET("api/events/{id}")
    suspend fun getEventDetails(@Path("id") eventId: Int): EventDetailsDto

    @POST("api/Registrations")
    suspend fun registerForEvent(@Body request: RegisterEventRequestDto): RegistrationResponseDto

    @DELETE("api/Registrations/event/{eventId}/cancel")
    suspend fun cancelRegistrationByEventId(@Path("eventId") eventId: Int)
}
