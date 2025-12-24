package com.example.eventapplication.domain.model

sealed class HomeError {
    object NetworkError : HomeError()
    object UnauthorizedError : HomeError()
    data class ServerError(val code: Int) : HomeError()
    object UnknownError : HomeError()
}
