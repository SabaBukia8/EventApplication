package com.example.eventapplication.presentation.extensions

import android.content.Context
import com.example.eventapplication.R
import com.example.eventapplication.domain.model.NetworkError
import com.example.eventapplication.domain.validation.EmailValidationError
import com.example.eventapplication.domain.validation.NameValidationError
import com.example.eventapplication.domain.validation.OtpValidationError
import com.example.eventapplication.domain.validation.PasswordValidationError
import com.example.eventapplication.domain.validation.PhoneValidationError

fun EmailValidationError.toStringResource(): Int {
    return when (this) {
        EmailValidationError.Empty -> R.string.error_email_empty
        EmailValidationError.InvalidFormat -> R.string.error_email_invalid
    }
}

fun PasswordValidationError.toStringResource(): Int {
    return when (this) {
        PasswordValidationError.Empty -> R.string.error_password_empty
        PasswordValidationError.TooShort -> R.string.error_password_too_short
        PasswordValidationError.NoUppercase -> R.string.error_password_no_uppercase
        PasswordValidationError.NoLowercase -> R.string.error_password_no_lowercase
        PasswordValidationError.NoDigit -> R.string.error_password_no_digit
    }
}

fun NameValidationError.toStringResource(isFirstName: Boolean = false): Int {
    return when (this) {
        NameValidationError.Empty -> 
            if (isFirstName) R.string.error_first_name_empty
            else R.string.error_last_name_empty
        NameValidationError.TooShort -> 
            if (isFirstName) R.string.error_first_name_short
            else R.string.error_last_name_short
    }
}

fun PhoneValidationError.toStringResource(): Int {
    return when (this) {
        PhoneValidationError.Empty -> R.string.error_phone_empty
        PhoneValidationError.InvalidFormat -> R.string.error_phone_invalid
    }
}

fun OtpValidationError.toStringResource(): Int {
    return when (this) {
        OtpValidationError.EMPTY -> R.string.error_otp_empty
        OtpValidationError.INVALID_LENGTH -> R.string.error_otp_invalid
        OtpValidationError.INVALID_FORMAT -> R.string.error_otp_invalid
    }
}

fun NetworkError.toStringResource(): Int {
    return when (this) {
        NetworkError.NoInternet -> R.string.error_no_internet
        NetworkError.Unauthorized -> R.string.error_unauthorized
        NetworkError.Forbidden -> R.string.error_forbidden
        NetworkError.NotFound -> R.string.error_not_found
        NetworkError.ServerError -> R.string.error_server_error
        NetworkError.Timeout -> R.string.error_timeout
        is NetworkError.Unknown -> R.string.error_unknown
        else -> R.string.error_unknown
    }
}

fun Context.getStringForEmailValidationError(error: EmailValidationError): String {
    return getString(error.toStringResource())
}

fun Context.getStringForPasswordValidationError(error: PasswordValidationError): String {
    return getString(error.toStringResource())
}

fun Context.getStringForNameValidationError(error: NameValidationError, isFirstName: Boolean = false): String {
    return getString(error.toStringResource(isFirstName))
}

fun Context.getStringForPhoneValidationError(error: PhoneValidationError): String {
    return getString(error.toStringResource())
}

fun Context.getStringForNetworkError(error: NetworkError): String {
    return when (error) {
        NetworkError.ServerError -> getString(R.string.error_server_error)
        is NetworkError.Unknown -> error.message ?: getString(R.string.error_unknown)
        else -> getString(error.toStringResource())
    }
}