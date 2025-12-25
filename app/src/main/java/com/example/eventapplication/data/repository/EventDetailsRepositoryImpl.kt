package com.example.eventapplication.data.repository

import com.example.eventapplication.data.common.HandleResponse
import com.example.eventapplication.data.mapper.toDomain
import com.example.eventapplication.data.mapper.toRegistrationStatus
import com.example.eventapplication.data.remote.api.EventDetailsApiService
import com.example.eventapplication.data.remote.api.EventRegistrationStatusApiService
import com.example.eventapplication.data.remote.api.UserRegistrationsApiService
import com.example.eventapplication.data.remote.dto.request.RegisterEventRequestDto
import com.example.eventapplication.domain.model.EventDetails
import com.example.eventapplication.domain.model.RegistrationStatus
import com.example.eventapplication.domain.repository.EventDetailsRepository
import com.example.eventapplication.domain.common.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class EventDetailsRepositoryImpl @Inject constructor(
    private val apiService: EventDetailsApiService,
    private val userRegistrationsApiService: UserRegistrationsApiService,
    private val registrationStatusApiService: EventRegistrationStatusApiService,
    private val handleResponse: HandleResponse
) : EventDetailsRepository {

    override fun getEventDetails(eventId: Int): Flow<Resource<EventDetails>> = flow {
        emit(Resource.Loader(true))

        try {
            // First, fetch event details
            val eventDetailsDto = apiService.getEventDetails(eventId)

            // Then, try to fetch registration status (may fail if not registered)
            val registrationStatus = try {
                val statusDto = registrationStatusApiService.getEventRegistrationStatus(eventId)
                android.util.Log.d("EventDetailsRepo", "Registration status DTO: $statusDto")
                val status = statusDto.registrationStatus?.toRegistrationStatus()
                android.util.Log.d("EventDetailsRepo", "Parsed registration status: $status")
                status
            } catch (e: Exception) {
                android.util.Log.e("EventDetailsRepo", "Failed to fetch registration status: ${e.message}", e)
                null // User not registered or API error
            }

            // Convert to domain model
            val eventDetails = eventDetailsDto.toDomain(registrationStatus)

            emit(Resource.Success(eventDetails))
        } catch (e: Exception) {
            android.util.Log.e("EventDetailsRepo", "Failed to fetch event details", e)
            emit(Resource.Error(com.example.eventapplication.domain.model.NetworkError.Unknown(e.message ?: "Unknown error")))
        } finally {
            emit(Resource.Loader(false))
        }
    }

    override fun registerForEvent(eventId: Int, userId: Int): Flow<Resource<Int>> {
        return handleResponse.safeApiCall {
            val requestDto = RegisterEventRequestDto(eventId, userId)
            val response = apiService.registerForEvent(requestDto)
            response.registrationId ?: 0 // Fallback to 0 if not provided by API
        }
    }

    override fun cancelRegistration(eventId: Int): Flow<Resource<Unit>> {
        return handleResponse.safeApiCall {
            apiService.cancelRegistrationByEventId(eventId)
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
