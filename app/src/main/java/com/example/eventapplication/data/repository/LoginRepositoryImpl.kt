package com.example.eventapplication.data.repository

import com.example.eventapplication.data.common.HandleResponse
import com.example.eventapplication.data.mapper.toDomain
import com.example.eventapplication.data.remote.api.LoginApiService
import com.example.eventapplication.data.remote.dto.request.LoginRequestDto
import com.example.eventapplication.domain.model.AuthResult
import com.example.eventapplication.domain.repository.LoginRepository
import com.example.eventapplication.domain.common.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val loginApiService: LoginApiService,
    private val handleResponse: HandleResponse
) : LoginRepository {

    override fun login(email: String, password: String): Flow<Resource<AuthResult>> {
        return handleResponse.safeApiCall {
            val requestDto = LoginRequestDto(email, password)
            loginApiService.login(requestDto).toDomain()
        }
    }
}
