package com.example.eventapplication.domain.usecase.auth

import com.example.eventapplication.domain.common.Resource
import com.example.eventapplication.domain.model.AuthResult
import com.example.eventapplication.domain.repository.LoginRepository
import com.example.eventapplication.domain.repository.TokenRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val loginRepository: LoginRepository,
    private val tokenRepository: TokenRepository
) {
    operator fun invoke(
        email: String,
        password: String,
        rememberMe: Boolean = true
    ): Flow<Resource<AuthResult>> {
        return loginRepository.login(email, password)
    }

    suspend fun saveToken(token: String) {
        tokenRepository.saveToken(token)
    }

    suspend fun saveUserData(authResult: AuthResult, email: String) {
        tokenRepository.saveUserData(authResult, email)
    }

    suspend fun setSessionPersistence(shouldPersist: Boolean) {
        tokenRepository.setSessionPersistence(shouldPersist)
    }
}
