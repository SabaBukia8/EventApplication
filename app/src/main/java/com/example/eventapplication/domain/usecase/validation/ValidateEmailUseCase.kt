package com.example.eventapplication.domain.usecase.validation

import com.example.eventapplication.domain.validation.EmailValidationError
import com.example.eventapplication.domain.validation.ValidationResult
import javax.inject.Inject

class ValidateEmailUseCase @Inject constructor() {

    operator fun invoke(email: String): ValidationResult<EmailValidationError> {
        if (email.isBlank()) {
            return ValidationResult.Error(EmailValidationError.Empty)
        }

        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        if (!email.matches(emailRegex)) {
            return ValidationResult.Error(EmailValidationError.InvalidFormat)
        }

        return ValidationResult.Success
    }
}
