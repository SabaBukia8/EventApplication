package com.example.eventapplication.presentation.extensions

import android.content.Context
import com.example.eventapplication.R
import com.example.eventapplication.domain.validation.EmailValidationError
import com.example.eventapplication.domain.validation.NameValidationError
import com.example.eventapplication.domain.validation.PasswordValidationError
import com.example.eventapplication.domain.validation.PhoneValidationError

fun EmailValidationError.toUiMessage(context: Context): String {
    return when (this) {
        EmailValidationError.Empty -> context.getString(R.string.error_email_empty)
        EmailValidationError.InvalidFormat -> context.getString(R.string.error_email_invalid)
    }
}

fun PasswordValidationError.toUiMessage(context: Context): String {
    return when (this) {
        PasswordValidationError.Empty -> context.getString(R.string.error_password_empty)
        PasswordValidationError.TooShort -> context.getString(R.string.error_password_too_short)
        PasswordValidationError.NoUppercase -> context.getString(R.string.error_password_no_uppercase)
        PasswordValidationError.NoLowercase -> context.getString(R.string.error_password_no_lowercase)
        PasswordValidationError.NoDigit -> context.getString(R.string.error_password_no_digit)
    }
}

fun NameValidationError.toUiMessage(context: Context, fieldName: String = "Name"): String {
    return when (this) {
        NameValidationError.Empty -> context.getString(R.string.error_name_empty, fieldName)
        NameValidationError.TooShort -> context.getString(R.string.error_name_too_short, fieldName)
    }
}

fun PhoneValidationError.toUiMessage(context: Context): String {
    return when (this) {
        PhoneValidationError.Empty -> context.getString(R.string.error_phone_empty)
        PhoneValidationError.InvalidFormat -> context.getString(R.string.error_phone_invalid)
    }
}
