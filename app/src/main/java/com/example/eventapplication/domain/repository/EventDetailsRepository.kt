package com.example.eventapplication.domain.repository

import com.example.eventapplication.domain.model.EventDetails
import com.example.eventapplication.domain.model.RegistrationStatus
import com.example.eventapplication.domain.common.Resource
import kotlinx.coroutines.flow.Flow

interface EventDetailsRepository {
    fun getEventDetails(eventId: Int): Flow<Resource<EventDetails>>

    fun registerForEvent(eventId: Int, userId: Int): Flow<Resource<Int>>

    fun cancelRegistration(registrationId: Int): Flow<Resource<Unit>>

    fun checkRegistrationStatus(eventId: Int, userId: Int): Flow<Resource<RegistrationStatus?>>
}
