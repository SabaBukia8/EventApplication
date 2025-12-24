package com.example.eventapplication.domain.validation

// Pure Kotlin - no Android dependencies
sealed class NameValidationError {
    data object Empty : NameValidationError()
    data object TooShort : NameValidationError()
}
