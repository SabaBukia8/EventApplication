package com.example.eventapplication.data.remote.api

import com.example.eventapplication.data.remote.dto.request.LoginRequestDto
import com.example.eventapplication.data.remote.dto.response.AuthResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginApiService {

    @POST("api/Auth/login")
    suspend fun login(@Body request: LoginRequestDto): AuthResponseDto
}
