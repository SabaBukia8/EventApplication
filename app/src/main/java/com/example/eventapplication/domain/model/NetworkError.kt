package com.example.eventapplication.domain.model

sealed class NetworkError {
    object NoInternet : NetworkError()
    object Unauthorized : NetworkError()
    object Forbidden : NetworkError()
    object NotFound : NetworkError()
    object ServerError : NetworkError()
    object Timeout : NetworkError()
    data class Unknown(val message: String? = null) : NetworkError()
}