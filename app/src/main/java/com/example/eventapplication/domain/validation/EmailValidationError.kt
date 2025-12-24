package com.example.eventapplication.domain.validation

// Pure Kotlin - no Android dependencies
sealed class EmailValidationError {
    data object Empty : EmailValidationError()
    data object InvalidFormat : EmailValidationError()
}
