package com.example.eventapplication.presentation.screen.home

import com.example.eventapplication.domain.model.Category
import com.example.eventapplication.domain.model.Event
import com.example.eventapplication.domain.model.HomeError
import com.example.eventapplication.domain.model.User

sealed class HomeState {
    object Idle : HomeState()
    data class IsLoading(val isLoading: Boolean) : HomeState()
    data class Success(
        val user: User,
        val upcomingEvents: List<Event>,
        val categories: List<Category>,
        val trendingEvents: List<Event>
    ) : HomeState()
    data class Error(val error: HomeError) : HomeState()
}

sealed class HomeEvent {
    object LoadData : HomeEvent()
    data class OnEventClicked(val eventId: Int) : HomeEvent()
    data class OnCategoryClicked(val categoryId: Int) : HomeEvent()
    object OnViewAllEventsClicked : HomeEvent()
    object OnNotificationClicked : HomeEvent()
    object OnLogoutClicked : HomeEvent()

    data class TestEventDetailsPage(val eventId: Int) : HomeEvent()
}

sealed class HomeSideEffect {
    data class NavigateToEventDetails(val eventId: Int) : HomeSideEffect()
    data class NavigateToCategory(val categoryId: Int) : HomeSideEffect()
    object NavigateToAllEvents : HomeSideEffect()
    object NavigateToNotifications : HomeSideEffect()
    object NavigateToLogin : HomeSideEffect()
    data class ShowError(val error: HomeError) : HomeSideEffect()
    data class ShowErrorMessage(@param:androidx.annotation.StringRes val messageResId: Int, val formatArgs: Array<Any> = emptyArray()) : HomeSideEffect()
}
