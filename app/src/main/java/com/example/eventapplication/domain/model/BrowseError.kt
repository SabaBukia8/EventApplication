package com.example.eventapplication.domain.model

sealed class BrowseError {
    object NetworkError : BrowseError()
    object UnauthorizedError : BrowseError()
    data class ServerError(val code: Int) : BrowseError()
    object NoResultsFound : BrowseError()
    object UnknownError : BrowseError()
}
