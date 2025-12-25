package com.example.eventapplication.domain.usecase.event

import com.example.eventapplication.domain.common.Resource
import com.example.eventapplication.domain.model.UserEventRegistration
import com.example.eventapplication.domain.repository.UserRegistrationsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserRegistrationsUseCase @Inject constructor(
    private val repository: UserRegistrationsRepository
) {
    operator fun invoke(): Flow<Resource<List<UserEventRegistration>>> {
        return repository.getUserRegistrations()
    }
}
