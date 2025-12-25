package com.example.eventapplication.domain.repository

import com.example.eventapplication.domain.common.Resource
import com.example.eventapplication.domain.model.UserEventRegistration
import kotlinx.coroutines.flow.Flow

interface UserRegistrationsRepository {

    fun getUserRegistrations(): Flow<Resource<List<UserEventRegistration>>>
}
