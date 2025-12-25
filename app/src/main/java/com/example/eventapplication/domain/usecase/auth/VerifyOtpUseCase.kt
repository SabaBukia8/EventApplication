package com.example.eventapplication.domain.usecase.auth

import com.example.eventapplication.domain.common.Resource
import com.example.eventapplication.domain.model.VerifyOtpResult
import com.example.eventapplication.domain.repository.RegisterRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class VerifyOtpUseCase @Inject constructor(
    private val registerRepository: RegisterRepository
) {
    operator fun invoke(phoneNumber: String, code: String): Flow<Resource<VerifyOtpResult>> {
        return registerRepository.verifyOtp(phoneNumber, code)
    }
}
