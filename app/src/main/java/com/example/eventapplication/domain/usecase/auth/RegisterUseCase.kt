package com.example.eventapplication.domain.usecase.auth

import com.example.eventapplication.domain.model.RegisterResult
import com.example.eventapplication.domain.repository.RegisterRepository
import com.example.eventapplication.domain.common.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val registerRepository: RegisterRepository
) {
    operator fun invoke(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        mobileNumber: String,
        departmentId: Int,
        confirmPassword: String
    ): Flow<Resource<RegisterResult>> {
        return registerRepository.register(
            email = email,
            password = password,
            firstName = firstName,
            lastName = lastName,
            mobileNumber = mobileNumber,
            departmentId = departmentId,
            confirmPassword = confirmPassword
        )
    }
}
