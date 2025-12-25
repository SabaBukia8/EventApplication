package com.example.eventapplication.domain.usecase.auth

import com.example.eventapplication.domain.repository.TokenRepository
import javax.inject.Inject

/**
 * Checks if the session should persist based on "Remember Me" setting.
 * If session should NOT persist, clears the token and user data.
 *
 * This is called on app cold start to implement proper session management.
 */
class CheckAndClearSessionUseCase @Inject constructor(
    private val tokenRepository: TokenRepository
) {
    suspend operator fun invoke() {
        val shouldPersist = tokenRepository.shouldSessionPersist()

        if (!shouldPersist) {
            // User did not check "Remember Me" during last login
            // Clear the session on app restart
            tokenRepository.clearToken()
        }
    }
}
