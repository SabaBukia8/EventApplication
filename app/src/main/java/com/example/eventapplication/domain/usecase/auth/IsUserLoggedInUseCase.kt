package com.example.eventapplication.domain.usecase.auth

import com.example.eventapplication.domain.repository.TokenRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class IsUserLoggedInUseCase @Inject constructor(
    private val tokenRepository: TokenRepository
) {
    operator fun invoke(): Flow<Boolean> {
        return tokenRepository.getToken().map { token ->
            !token.isNullOrEmpty()
        }
    }
}
