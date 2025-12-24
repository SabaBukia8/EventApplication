package com.example.eventapplication.data.remote.api

import com.example.eventapplication.data.remote.dto.response.UserEventRegistrationDto
import retrofit2.http.GET
import retrofit2.http.Path

interface UserRegistrationsApiService {

    @GET("api/Registrations/user/{userId}")
    suspend fun getUserRegistrations(@Path("userId") userId: Int): List<UserEventRegistrationDto>
}
