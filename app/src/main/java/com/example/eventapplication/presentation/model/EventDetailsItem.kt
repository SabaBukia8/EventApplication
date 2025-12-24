package com.example.eventapplication.presentation.model

import com.example.eventapplication.domain.model.AgendaItem
import com.example.eventapplication.domain.model.EventDetails
import com.example.eventapplication.domain.model.EventType
import com.example.eventapplication.domain.model.Speaker

sealed class EventDetailsItem {
    data class Image(
        val imageUrl: String?,
        val eventType: EventType
    ) : EventDetailsItem()

    data class Info(
        val eventDetails: EventDetails
    ) : EventDetailsItem()

    data class Action(
        val buttonText: String,
        val isEnabled: Boolean,
        val capacityText: String,
        val isRegistering: Boolean,
        val registrationDeadline: String? = null
    ) : EventDetailsItem()

    data class Description(
        val description: String
    ) : EventDetailsItem()

    data class AgendaSection(
        val agendaItems: List<AgendaItem>
    ) : EventDetailsItem()

    data class SpeakersSection(
        val speakers: List<Speaker>
    ) : EventDetailsItem()
}