package com.example.eventapplication.domain.usecase.auth

import com.example.eventapplication.domain.common.Resource
import com.example.eventapplication.domain.model.User
import com.example.eventapplication.domain.repository.TokenRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserProfileUseCase @Inject constructor(
    private val tokenRepository: TokenRepository
) {
    operator fun invoke(userId: Int): Flow<Resource<User>> {
        return tokenRepository.getCurrentUser()
    }
}