package com.example.eventapplication.domain.repository

import com.example.eventapplication.domain.common.Resource
import com.example.eventapplication.domain.model.EventRegistrationStatus
import kotlinx.coroutines.flow.Flow

interface RegistrationsRepository {
    fun getEventRegistrationStatus(eventId: Int): Flow<Resource<EventRegistrationStatus>>
}
