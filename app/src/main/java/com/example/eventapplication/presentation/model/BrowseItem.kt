package com.example.eventapplication.presentation.model

import com.example.eventapplication.domain.model.Category
import com.example.eventapplication.domain.model.Event

sealed class BrowseItem {
    data class Header(
        val searchQuery: String,
        val hasActiveFilters: Boolean
    ) : BrowseItem()

    data class Categories(
        val categories: List<Category>,
        val selectedCategoryId: Int?
    ) : BrowseItem()

    data class EventCard(val event: Event) : BrowseItem()

    object EmptyState : BrowseItem()
}