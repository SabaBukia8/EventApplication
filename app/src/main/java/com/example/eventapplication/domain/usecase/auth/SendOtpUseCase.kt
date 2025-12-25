package com.example.eventapplication.domain.usecase.auth

import com.example.eventapplication.domain.common.Resource
import com.example.eventapplication.domain.model.SendOtpResult
import com.example.eventapplication.domain.repository.RegisterRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SendOtpUseCase @Inject constructor(
    private val registerRepository: RegisterRepository
) {
    operator fun invoke(phoneNumber: String): Flow<Resource<SendOtpResult>> {
        return registerRepository.sendOtp(phoneNumber)
    }
}
