package com.example.eventapplication.data.remote.api

import com.example.eventapplication.data.remote.dto.request.RegisterRequestDto
import com.example.eventapplication.data.remote.dto.response.RegisterResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface RegistrationApiService {

    @POST("api/Auth/register")
    suspend fun register(@Body request: RegisterRequestDto): RegisterResponseDto
}
