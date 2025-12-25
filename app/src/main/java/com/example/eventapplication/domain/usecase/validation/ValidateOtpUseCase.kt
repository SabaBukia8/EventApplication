package com.example.eventapplication.domain.usecase.validation

import com.example.eventapplication.domain.validation.OtpValidationError
import com.example.eventapplication.domain.validation.ValidationResult
import javax.inject.Inject

class ValidateOtpUseCase @Inject constructor() {
    operator fun invoke(otp: String): ValidationResult<OtpValidationError> {
        if (otp.isBlank()) {
            return ValidationResult.Error(OtpValidationError.EMPTY)
        }

        if (otp.length != 6) {
            return ValidationResult.Error(OtpValidationError.INVALID_LENGTH)
        }

        if (!otp.all { it.isDigit() }) {
            return ValidationResult.Error(OtpValidationError.INVALID_FORMAT)
        }

        return ValidationResult.Success
    }
}
