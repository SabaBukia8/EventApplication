package com.example.eventapplication.presentation.extensions

import com.example.eventapplication.R
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





