package com.example.eventapplication.domain.usecase.auth

import com.example.eventapplication.domain.repository.TokenRepository
import javax.inject.Inject


class CheckAndClearSessionUseCase @Inject constructor(
    private val tokenRepository: TokenRepository
) {
    suspend operator fun invoke() {
        val shouldPersist = tokenRepository.shouldSessionPersist()

        if (!shouldPersist) {
            tokenRepository.clearToken()
        }
    }
}
