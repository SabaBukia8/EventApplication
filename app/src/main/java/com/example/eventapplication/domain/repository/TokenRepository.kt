package com.example.eventapplication.domain.repository

import com.example.eventapplication.domain.common.Resource
import com.example.eventapplication.domain.model.AuthResult
import com.example.eventapplication.domain.model.User
import kotlinx.coroutines.flow.Flow

interface TokenRepository {
    suspend fun saveToken(token: String)
    fun getToken(): Flow<String?>
    suspend fun clearToken()
    suspend fun saveUserData(authResult: AuthResult, email: String)
    fun getCurrentUser(): Flow<Resource<User>>
}
