package com.example.eventapplication.domain.usecase.auth

import com.example.eventapplication.domain.common.Resource
import com.example.eventapplication.domain.model.User
import com.example.eventapplication.domain.repository.TokenRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val tokenRepository: TokenRepository
) {
    operator fun invoke(): Flow<Resource<User>> {
        return tokenRepository.getCurrentUser()
    }
}
