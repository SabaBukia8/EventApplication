package com.example.eventapplication.domain.validation

sealed class ValidationResult<out E> {
    data object Success : ValidationResult<Nothing>()
    data class Error<E>(val error: E) : ValidationResult<E>()
}
