package com.example.eventapplication.domain.repository

import com.example.eventapplication.domain.common.Resource
import com.example.eventapplication.domain.model.RegisterResult
import com.example.eventapplication.domain.model.SendOtpResult
import com.example.eventapplication.domain.model.VerifyOtpResult
import kotlinx.coroutines.flow.Flow

interface RegisterRepository {
    fun register(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        mobileNumber: String,
        departmentId: Int,
        confirmPassword: String
    ): Flow<Resource<RegisterResult>>

    fun sendOtp(phoneNumber: String): Flow<Resource<SendOtpResult>>

    fun verifyOtp(phoneNumber: String, code: String): Flow<Resource<VerifyOtpResult>>
}
