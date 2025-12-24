package com.example.eventapplication.domain.validation

sealed class PasswordValidationError {
    data object Empty : PasswordValidationError()
    data object TooShort : PasswordValidationError()
    data object NoUppercase : PasswordValidationError()
    data object NoLowercase : PasswordValidationError()
    data object NoDigit : PasswordValidationError()
}
