package com.example.eventapplication.presentation.model

import com.example.eventapplication.domain.model.Category
import com.example.eventapplication.domain.model.Event

sealed class HomeItem {
    data class Header(
        val userName: String,
        val hasNotifications: Boolean
    ) : HomeItem()

    data class Welcome(val userName: String) : HomeItem()

    data class UpcomingEventsSection(
        val events: List<Event>
    ) : HomeItem()

    data class CategoriesSection(
        val categories: List<Category>
    ) : HomeItem()

    data class TrendingEventsSection(
        val events: List<Event>
    ) : HomeItem()
}