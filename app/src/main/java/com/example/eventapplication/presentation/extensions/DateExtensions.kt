package com.example.eventapplication.presentation.extensions

import java.text.SimpleDateFormat
import java.util.*

fun String.toFormattedDate(): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val date = inputFormat.parse(this.replace("Z", "")) ?: return ""
        val outputFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
        outputFormat.format(date)
    } catch (e: Exception) {
        ""
    }
}

fun String.toFormattedTime(): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val date = inputFormat.parse(this) ?: return ""
        val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        outputFormat.format(date)
    } catch (e: Exception) {
        ""
    }
}

fun String.toFormattedDateRange(endDateTime: String? = null): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val startDate = inputFormat.parse(this.replace("Z", "")) ?: return ""

        if (endDateTime == null) {
            val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            return outputFormat.format(startDate)
        }

        val endDate = inputFormat.parse(endDateTime.replace("Z", "")) ?: return ""
        val startCal = Calendar.getInstance().apply { time = startDate }
        val endCal = Calendar.getInstance().apply { time = endDate }

        if (startCal.get(Calendar.YEAR) == endCal.get(Calendar.YEAR) &&
            startCal.get(Calendar.DAY_OF_YEAR) == endCal.get(Calendar.DAY_OF_YEAR)) {
            val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            return outputFormat.format(startDate)
        }

        if (startCal.get(Calendar.YEAR) == endCal.get(Calendar.YEAR) &&
            startCal.get(Calendar.MONTH) == endCal.get(Calendar.MONTH)) {
            val monthFormat = SimpleDateFormat("MMM", Locale.getDefault())
            val startDay = startCal.get(Calendar.DAY_OF_MONTH)
            val endDay = endCal.get(Calendar.DAY_OF_MONTH)
            val year = startCal.get(Calendar.YEAR)
            return "${monthFormat.format(startDate)} $startDay-$endDay, $year"
        }

        val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return "${outputFormat.format(startDate)} - ${outputFormat.format(endDate)}"
    } catch (e: Exception) {
        ""
    }
}

fun Pair<String, String>.toTimeRange(): String {
    val startTime = first.toFormattedTime()
    val endTime = second.toFormattedTime()
    return if (startTime.isNotEmpty() && endTime.isNotEmpty()) {
        "$startTime - $endTime"
    } else {
        ""
    }
}

fun String.toFormattedDateTime(): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val date = inputFormat.parse(this) ?: return ""
        val outputFormat = SimpleDateFormat("EEEE, MMM dd", Locale.getDefault())
        outputFormat.format(date)
    } catch (e: Exception) {
        ""
    }
}

fun String.toRegistrationDeadlineText(): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val date = inputFormat.parse(this) ?: return ""
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        "Registration closes on ${dateFormat.format(date)} at ${timeFormat.format(date)}."
    } catch (e: Exception) {
        ""
    }
}

fun String.toMonthAbbreviation(): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val date = inputFormat.parse(this.replace("Z", "")) ?: return ""
        val outputFormat = SimpleDateFormat("MMM", Locale.getDefault())
        outputFormat.format(date).uppercase()
    } catch (e: Exception) {
        ""
    }
}

fun String.toDayOfMonth(): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val date = inputFormat.parse(this.replace("Z", "")) ?: return ""
        val outputFormat = SimpleDateFormat("dd", Locale.getDefault())
        outputFormat.format(date)
    } catch (e: Exception) {
        ""
    }
}

fun String.toTimeString(endDateTime: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val startDate = inputFormat.parse(this.replace("Z", "")) ?: return ""
        val endDate = inputFormat.parse(endDateTime.replace("Z", "")) ?: return ""
        val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        "${outputFormat.format(startDate)} - ${outputFormat.format(endDate)}"
    } catch (e: Exception) {
        ""
    }
}
