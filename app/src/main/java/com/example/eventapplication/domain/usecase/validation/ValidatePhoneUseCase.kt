package com.example.eventapplication.domain.usecase.validation

import com.example.eventapplication.domain.validation.PhoneValidationError
import com.example.eventapplication.domain.validation.ValidationResult
import javax.inject.Inject

class ValidatePhoneUseCase @Inject constructor() {

    operator fun invoke(phone: String): ValidationResult<PhoneValidationError> {
        if (phone.isBlank()) {
            return ValidationResult.Error(PhoneValidationError.Empty)
        }
        val phonePattern =
            "^[+]?[0-9]{10,13}$|^[+]?\\(?[0-9]{3}\\)?[\\s.-]?[0-9]{3}[\\s.-]?[0-9]{4}$".toRegex()
        if (!phone.matches(phonePattern)) {
            return ValidationResult.Error(PhoneValidationError.InvalidFormat)
        }

        return ValidationResult.Success
    }
}
