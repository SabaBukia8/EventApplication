package com.example.eventapplication.domain.model

data class EventFilters(
    val location: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val onlyAvailable: Boolean = false
) {
    val hasActiveFilters: Boolean
        get() = location != null || startDate != null || endDate != null || onlyAvailable
}
