package com.example.eventapplication.domain.model

data class Event(
    val id: Int,
    val title: String,
    val description: String,
    val eventType: EventType,
    val startDateTime: String,
    val endDateTime: String,
    val location: String,
    val capacity: Int,
    val confirmedCount: Int,
    val isFull: Boolean,
    val isWaitlisted: Boolean = false,
    val registrationStatus: RegistrationStatus? = null,
    val imageUrl: String? = null,
    val organizer: Organizer? = null
) {
    val spotsLeft: Int
        get() = (capacity - confirmedCount).coerceAtLeast(0)
}

enum class EventType {
    TEAM_BUILDING,
    SPORTS,
    WORKSHOP,
    HAPPY_FRIDAY,
    CULTURAL,
    WELLNESS,
    TRAINING,
    SOCIAL,
    CONFERENCE,
    OTHER
}
