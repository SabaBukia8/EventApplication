package com.example.eventapplication.data.repository

import com.example.eventapplication.data.common.HandleResponse
import com.example.eventapplication.data.mapper.toDomain
import com.example.eventapplication.data.remote.api.EventRegistrationStatusApiService
import com.example.eventapplication.domain.common.Resource
import com.example.eventapplication.domain.model.EventRegistrationStatus
import com.example.eventapplication.domain.repository.RegistrationsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RegistrationsRepositoryImpl @Inject constructor(
    private val apiService: EventRegistrationStatusApiService,
    private val handleResponse: HandleResponse
) : RegistrationsRepository {

    override fun getEventRegistrationStatus(eventId: Int): Flow<Resource<EventRegistrationStatus>> {
        return handleResponse.safeApiCall {
            apiService.getEventRegistrationStatus(eventId).toDomain()
        }
    }
}
