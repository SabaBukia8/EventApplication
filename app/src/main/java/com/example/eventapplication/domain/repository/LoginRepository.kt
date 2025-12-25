package com.example.eventapplication.domain.repository

import com.example.eventapplication.domain.common.Resource
import com.example.eventapplication.domain.model.AuthResult
import kotlinx.coroutines.flow.Flow

interface LoginRepository {
    fun login(email: String, password: String): Flow<Resource<AuthResult>>
}
