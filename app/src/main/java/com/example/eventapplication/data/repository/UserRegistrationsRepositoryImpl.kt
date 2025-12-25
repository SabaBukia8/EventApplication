package com.example.eventapplication.data.repository

import com.example.eventapplication.data.common.HandleResponse
import com.example.eventapplication.data.mapper.toDomain
import com.example.eventapplication.data.remote.api.UserRegistrationsApiService
import com.example.eventapplication.domain.common.Resource
import com.example.eventapplication.domain.model.UserEventRegistration
import com.example.eventapplication.domain.repository.UserRegistrationsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserRegistrationsRepositoryImpl @Inject constructor(
    private val apiService: UserRegistrationsApiService,
    private val handleResponse: HandleResponse
) : UserRegistrationsRepository {

    override fun getUserRegistrations(): Flow<Resource<List<UserEventRegistration>>> {
        return handleResponse.safeApiCall {
            apiService.getUserRegistrations().toDomain()
        }
    }
}
