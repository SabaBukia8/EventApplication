package com.example.eventapplication.presentation.screen.home.mapper

import android.content.Context
import com.example.eventapplication.R
import com.example.eventapplication.domain.model.EventType
object EventTypeMapper {
    fun EventType.toIconRes(): Int {
        return when (this) {
            EventType.TEAM_BUILDING -> R.drawable.ic_team_building
            EventType.SPORTS -> R.drawable.ic_sports
            EventType.WORKSHOP -> R.drawable.ic_workshops
            EventType.HAPPY_FRIDAY -> R.drawable.ic_happy_fridays
            EventType.CULTURAL -> R.drawable.ic_cultural
            EventType.WELLNESS -> R.drawable.ic_wellness
        }
    }

    fun EventType.toPlaceholderColor(): Int {
        return when (this) {
            EventType.TEAM_BUILDING -> R.color.placeholder_medium
            EventType.SPORTS -> R.color.placeholder_light
            EventType.WORKSHOP -> R.color.placeholder_dark
            EventType.HAPPY_FRIDAY -> R.color.placeholder_medium
            EventType.CULTURAL -> R.color.placeholder_light
            EventType.WELLNESS -> R.color.placeholder_dark
        }
    }

    fun EventType.toDisplayName(context: Context): String {
        return when (this) {
            EventType.TEAM_BUILDING -> context.getString(R.string.category_team_building)
            EventType.SPORTS -> context.getString(R.string.category_sports)
            EventType.WORKSHOP -> context.getString(R.string.category_workshops)
            EventType.HAPPY_FRIDAY -> context.getString(R.string.category_happy_fridays)
            EventType.CULTURAL -> context.getString(R.string.category_cultural)
            EventType.WELLNESS -> context.getString(R.string.category_wellness)
        }
    }
}
