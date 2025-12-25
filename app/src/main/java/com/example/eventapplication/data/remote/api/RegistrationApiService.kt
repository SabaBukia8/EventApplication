package com.example.eventapplication.data.remote.api

import com.example.eventapplication.data.remote.dto.request.RegisterRequestDto
import com.example.eventapplication.data.remote.dto.request.SendOtpRequestDto
import com.example.eventapplication.data.remote.dto.request.VerifyOtpRequestDto
import com.example.eventapplication.data.remote.dto.response.RegisterResponseDto
import com.example.eventapplication.data.remote.dto.response.SendOtpResponseDto
import com.example.eventapplication.data.remote.dto.response.VerifyOtpResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface RegistrationApiService {

    @POST("api/Auth/register")
    suspend fun register(@Body request: RegisterRequestDto): RegisterResponseDto

    @POST("api/Auth/send-otp")
    suspend fun sendOtp(@Body request: SendOtpRequestDto): SendOtpResponseDto

    @POST("api/Auth/verify-otp")
    suspend fun verifyOtp(@Body request: VerifyOtpRequestDto): VerifyOtpResponseDto
}
