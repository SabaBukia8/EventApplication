package com.example.eventapplication.domain.validation

sealed class PhoneValidationError {
    data object Empty : PhoneValidationError()
    data object InvalidFormat : PhoneValidationError()
}
