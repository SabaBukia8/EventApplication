package com.example.eventapplication.data.repository

import com.example.eventapplication.data.common.HandleResponse
import com.example.eventapplication.data.mapper.toDomain
import com.example.eventapplication.data.remote.api.RegistrationApiService
import com.example.eventapplication.data.remote.dto.request.RegisterRequestDto
import com.example.eventapplication.domain.model.RegisterResult
import com.example.eventapplication.domain.repository.RegisterRepository
import com.example.eventapplication.domain.common.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RegisterRepositoryImpl @Inject constructor(
    private val registrationApiService: RegistrationApiService,
    private val handleResponse: HandleResponse
) : RegisterRepository {

    override fun register(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        mobileNumber: String,
        departmentId: Int,
        confirmPassword: String
    ): Flow<Resource<RegisterResult>> {
        return handleResponse.safeApiCall {
            val requestDto = RegisterRequestDto(
                firstName = firstName,
                lastName = lastName,
                email = email,
                mobileNumber = mobileNumber,
                departmentId = departmentId,
                password = password,
                confirmPassword = confirmPassword
            )
            registrationApiService.register(requestDto).toDomain()
        }
    }
}
