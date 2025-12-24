package com.example.eventapplication.domain.model

sealed class NotificationsError : NetworkError() {
    data object NetworkUnavailable : NotificationsError()
    data object Unauthorized : NotificationsError()
    data object ServerError : NotificationsError()
    data class Unknown(val message: String) : NotificationsError()
}
