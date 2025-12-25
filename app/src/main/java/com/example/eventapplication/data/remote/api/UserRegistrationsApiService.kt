package com.example.eventapplication.data.remote.api

import com.example.eventapplication.data.remote.dto.response.UserEventRegistrationDto
import retrofit2.http.GET

interface UserRegistrationsApiService {

    @GET("api/Registrations/user/me")
    suspend fun getUserRegistrations(): List<UserEventRegistrationDto>
}