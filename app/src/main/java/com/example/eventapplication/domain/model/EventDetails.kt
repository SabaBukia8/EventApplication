package com.example.eventapplication.domain.model

data class EventDetails(
    val id: Int,
    val title: String,
    val description: String,
    val eventType: EventType,
    val startDateTime: String,
    val endDateTime: String,
    val location: String,
    val capacity: Int,
    val confirmedCount: Int,
    val waitlistedCount: Int,
    val isFull: Boolean,
    val registrationStatus: RegistrationStatus?,
    val imageUrl: String?,
    val organizer: Organizer,
    val tags: List<String>,
    val agenda: List<AgendaItem>,
    val speakers: List<Speaker>,
    val registrationDeadline: String? = null
) {
    val spotsLeft: Int
        get() = (capacity - confirmedCount).coerceAtLeast(0)

    val capacityText: String
        get() = "$confirmedCount/$capacity Registered"
}

data class AgendaItem(
    val id: Int,
    val step: Int,
    val title: String,
    val time: String,
    val description: String
)

data class Speaker(
    val id: Int,
    val name: String,
    val title: String,
    val avatarUrl: String?
)
