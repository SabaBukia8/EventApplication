package com.example.eventapplication.data.repository

import com.example.eventapplication.data.common.HandleResponse
import com.example.eventapplication.data.mapper.toDomain
import com.example.eventapplication.data.mapper.toRegistrationStatus
import com.example.eventapplication.data.remote.api.EventDetailsApiService
import com.example.eventapplication.data.remote.api.UserRegistrationsApiService
import com.example.eventapplication.data.remote.dto.request.RegisterEventRequestDto
import com.example.eventapplication.domain.model.EventDetails
import com.example.eventapplication.domain.model.RegistrationStatus
import com.example.eventapplication.domain.repository.EventDetailsRepository
import com.example.eventapplication.domain.common.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class EventDetailsRepositoryImpl @Inject constructor(
    private val apiService: EventDetailsApiService,
    private val userRegistrationsApiService: UserRegistrationsApiService,
    private val handleResponse: HandleResponse
) : EventDetailsRepository {

    override fun getEventDetails(eventId: Int): Flow<Resource<EventDetails>> {
        return handleResponse.safeApiCall {
            apiService.getEventDetails(eventId)
        }.map { resource ->
            when (resource) {
                is Resource.Success -> {
                    Resource.Success(resource.data.toDomain())
                }
                is Resource.Error -> resource
                is Resource.Loader -> resource
            }
        }
    }

    override fun registerForEvent(eventId: Int, userId: Int): Flow<Resource<Int>> {
        return handleResponse.safeApiCall {
            val requestDto = RegisterEventRequestDto(eventId, userId)
            val response = apiService.registerForEvent(requestDto)
            response.registrationId
        }
    }

    override fun cancelRegistration(registrationId: Int): Flow<Resource<Unit>> {
        return handleResponse.safeApiCall {
            apiService.cancelRegistration(registrationId)
        }
    }

    override fun checkRegistrationStatus(
        eventId: Int,
        userId: Int
    ): Flow<Resource<RegistrationStatus?>> {
        return handleResponse.safeApiCall {
            val allRegistrations = userRegistrationsApiService.getUserRegistrations()
            val eventRegistration = allRegistrations.firstOrNull { it.eventId == eventId }
            eventRegistration?.status?.toRegistrationStatus()
        }
    }
}
