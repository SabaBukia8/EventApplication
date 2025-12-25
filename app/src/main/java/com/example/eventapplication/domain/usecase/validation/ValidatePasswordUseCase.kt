package com.example.eventapplication.domain.usecase.validation

import com.example.eventapplication.domain.validation.PasswordValidationError
import com.example.eventapplication.domain.validation.ValidationResult
import javax.inject.Inject

class ValidatePasswordUseCase @Inject constructor() {

    operator fun invoke(password: String): ValidationResult<PasswordValidationError> {
        if (password.isBlank()) {
            return ValidationResult.Error(PasswordValidationError.Empty)
        }

        if (password.length < 8) {
            return ValidationResult.Error(PasswordValidationError.TooShort)
        }

        if (!password.any { it.isUpperCase() }) {
            return ValidationResult.Error(PasswordValidationError.NoUppercase)
        }

        if (!password.any { it.isLowerCase() }) {
            return ValidationResult.Error(PasswordValidationError.NoLowercase)
        }

        if (!password.any { it.isDigit() }) {
            return ValidationResult.Error(PasswordValidationError.NoDigit)
        }

        return ValidationResult.Success
    }
}
