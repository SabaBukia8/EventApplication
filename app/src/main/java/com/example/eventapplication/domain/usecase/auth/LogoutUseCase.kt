package com.example.eventapplication.domain.usecase.auth

import com.example.eventapplication.domain.repository.TokenRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val tokenRepository: TokenRepository
) {
    suspend operator fun invoke() {
        tokenRepository.clearToken()
        tokenRepository.setSessionPersistence(false)
    }
}
