package com.example.eventapplication.presentation.extensions

import android.content.Context
import com.example.eventapplication.R
import com.example.eventapplication.domain.model.BrowseError
import com.example.eventapplication.domain.model.CategoryEventsError
import com.example.eventapplication.domain.model.EventDetailsError
import com.example.eventapplication.domain.model.HomeError

fun HomeError.toErrorMessage(context: Context): String {
    return when (this) {
        is HomeError.NetworkError -> context.getString(R.string.error_network)
        is HomeError.UnauthorizedError -> context.getString(R.string.error_unauthorized)
        is HomeError.ServerError -> context.getString(R.string.error_server, code)
        is HomeError.UnknownError -> context.getString(R.string.error_unknown)
    }
}

fun EventDetailsError.toErrorMessage(context: Context): String {
    return when (this) {
        is EventDetailsError.NetworkError -> context.getString(R.string.error_network)
        is EventDetailsError.UnauthorizedError -> context.getString(R.string.error_unauthorized)
        is EventDetailsError.EventNotFound -> context.getString(R.string.error_event_not_found)
        is EventDetailsError.EventFull -> context.getString(R.string.error_event_full)
        is EventDetailsError.AlreadyRegistered -> context.getString(R.string.error_already_registered)
        is EventDetailsError.RegistrationNotFound -> context.getString(R.string.error_registration_not_found)
        is EventDetailsError.ServerError -> context.getString(R.string.error_server, code)
        is EventDetailsError.UnknownError -> context.getString(R.string.error_unknown)
    }
}


fun BrowseError.toErrorMessage(context: Context): String {
    return when (this) {
        is BrowseError.NetworkError -> context.getString(R.string.error_network)
        is BrowseError.UnauthorizedError -> context.getString(R.string.error_unauthorized)
        is BrowseError.ServerError -> context.getString(R.string.error_server, code)
        is BrowseError.NoResultsFound -> context.getString(R.string.browse_no_results)
        is BrowseError.UnknownError -> context.getString(R.string.error_unknown)
    }
}

fun CategoryEventsError.toErrorMessage(context: Context): String {
    return when (this) {
        is CategoryEventsError.NetworkError -> context.getString(R.string.error_network)
        is CategoryEventsError.UnauthorizedError -> context.getString(R.string.error_unauthorized)
        is CategoryEventsError.ServerError -> context.getString(R.string.error_server_error)
        is CategoryEventsError.CategoryNotFound -> context.getString(R.string.error_category_not_found)
        is CategoryEventsError.NoEventsFound -> context.getString(R.string.error_no_events_found)
        is CategoryEventsError.UnknownError -> message ?: context.getString(R.string.error_unknown)
    }
}
