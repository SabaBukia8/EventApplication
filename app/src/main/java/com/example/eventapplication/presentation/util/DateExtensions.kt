package com.example.eventapplication.presentation.util

import com.example.eventapplication.presentation.model.DateCategory
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun String.toTimeAgo(): String {
    return try {
        val dateTime = LocalDateTime.parse(this, DateTimeFormatter.ISO_DATE_TIME)
        val now = LocalDateTime.now()
        val duration = Duration.between(dateTime, now)

        when {
            duration.toMinutes() < 1 -> "Just now"
            duration.toMinutes() < 60 -> "${duration.toMinutes()}m ago"
            duration.toHours() < 24 -> "${duration.toHours()}h ago"
            duration.toDays() == 1L -> "Yesterday"
            duration.toDays() < 7 -> "${duration.toDays()}d ago"
            else -> dateTime.format(DateTimeFormatter.ofPattern("MMM dd", Locale.getDefault()))
        }
    } catch (e: Exception) {
        this
    }
}

fun String.getDateCategory(): DateCategory {
    return try {
        val dateTime = LocalDateTime.parse(this, DateTimeFormatter.ISO_DATE_TIME)
        val date = dateTime.toLocalDate()
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)

        when {
            date.isEqual(today) -> DateCategory.TODAY
            date.isEqual(yesterday) -> DateCategory.YESTERDAY
            else -> DateCategory.EARLIER
        }
    } catch (e: Exception) {
        DateCategory.EARLIER
    }
}

fun String.toFormattedDateTime(): String {
    return try {
        val dateTime = LocalDateTime.parse(this, DateTimeFormatter.ISO_DATE_TIME)
        dateTime.format(
            DateTimeFormatter.ofPattern(
                "MMM dd, yyyy 'at' hh:mm a",
                Locale.getDefault()
            )
        )
    } catch (e: Exception) {
        this
    }
}
