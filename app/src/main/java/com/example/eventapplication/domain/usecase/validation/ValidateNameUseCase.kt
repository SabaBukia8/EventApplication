package com.example.eventapplication.domain.usecase.validation

import com.example.eventapplication.domain.validation.NameValidationError
import com.example.eventapplication.domain.validation.ValidationResult
import javax.inject.Inject

class ValidateNameUseCase @Inject constructor() {

    operator fun invoke(name: String): ValidationResult<NameValidationError> {
        if (name.isBlank()) {
            return ValidationResult.Error(NameValidationError.Empty)
        }

        if (name.length < 2) {
            return ValidationResult.Error(NameValidationError.TooShort)
        }

        return ValidationResult.Success
    }
}
