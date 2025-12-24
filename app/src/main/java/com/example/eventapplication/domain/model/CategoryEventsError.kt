package com.example.eventapplication.domain.model

sealed class CategoryEventsError {
    object NetworkError : CategoryEventsError()
    object UnauthorizedError : CategoryEventsError()
    object ServerError : CategoryEventsError()
    object CategoryNotFound : CategoryEventsError()
    object NoEventsFound : CategoryEventsError()
    data class UnknownError(val message: String? = null) : CategoryEventsError()
}
