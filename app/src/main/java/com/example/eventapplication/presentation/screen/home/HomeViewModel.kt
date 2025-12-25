package com.example.eventapplication.presentation.screen.home

import androidx.lifecycle.viewModelScope
import com.example.eventapplication.R
import com.example.eventapplication.domain.common.Resource
import com.example.eventapplication.domain.model.Category
import com.example.eventapplication.domain.model.Event
import com.example.eventapplication.domain.model.User
import com.example.eventapplication.domain.usecase.auth.GetUserProfileUseCase
import com.example.eventapplication.domain.usecase.event.GetCategoriesUseCase
import com.example.eventapplication.domain.usecase.event.GetTrendingEventsUseCase
import com.example.eventapplication.domain.usecase.event.GetUpcomingEventsUseCase
import com.example.eventapplication.presentation.common.BaseViewModel
import com.example.eventapplication.presentation.util.StringResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getUpcomingEventsUseCase: GetUpcomingEventsUseCase,
    private val getTrendingEventsUseCase: GetTrendingEventsUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getEventRegistrationStatusUseCase: com.example.eventapplication.domain.usecase.event.GetEventRegistrationStatusUseCase,
    private val logoutUseCase: com.example.eventapplication.domain.usecase.auth.LogoutUseCase,
    private val stringProvider: StringResourceProvider
) : BaseViewModel<HomeState, HomeEvent, HomeSideEffect>(
    initialState = HomeState.Idle
) {


    override fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.LoadData -> loadData()
            is HomeEvent.OnEventClicked -> navigateToEventDetails(event.eventId)
            is HomeEvent.OnCategoryClicked -> navigateToCategory(event.categoryId)
            is HomeEvent.OnViewAllEventsClicked -> navigateToAllEvents()
            is HomeEvent.OnNotificationClicked -> navigateToNotifications()
            is HomeEvent.OnLogoutClicked -> handleLogout()
            is HomeEvent.TestEventDetailsPage -> navigateToEventDetails(event.eventId)
        }
    }

    private fun loadData() {
        updateState { HomeState.IsLoading(true) }

        viewModelScope.launch {
            var user: User? = null
            var upcomingEvents: List<Event> = emptyList()
            var trendingEvents: List<Event> = emptyList()
            var categories: List<Category> = emptyList()

            try {
                val userResult = getUserProfileUseCase(userId = 1).first {
                    it is Resource.Success || it is Resource.Error
                }
                if (userResult is Resource.Success) {
                    user = userResult.data
                }
            } catch (_: Exception) {
            }

            try {
                val upcomingResult = getUpcomingEventsUseCase(limit = 5).first {
                    it is Resource.Success || it is Resource.Error
                }
                if (upcomingResult is Resource.Success) {
                    upcomingEvents = upcomingResult.data
                    android.util.Log.d(
                        "HomeViewModel",
                        "Upcoming events loaded: ${upcomingEvents.size}"
                    )

                    upcomingEvents = upcomingEvents.map { event ->
                        try {
                            val statusResult = getEventRegistrationStatusUseCase(event.id).first {
                                it is Resource.Success || it is Resource.Error
                            }
                            if (statusResult is Resource.Success) {
                                val status = statusResult.data
                                event.copy(registrationStatus = status.status)
                            } else {
                                event
                            }
                        } catch (e: Exception) {
                            android.util.Log.e(
                                "HomeViewModel",
                                "Error loading registration status for event ${event.id}",
                                e
                            )
                            event
                        }
                    }
                } else if (upcomingResult is Resource.Error) {
                    android.util.Log.e(
                        "HomeViewModel",
                        "Error loading upcoming events: ${upcomingResult.error}"
                    )
                }
            } catch (e: Exception) {
                android.util.Log.e("HomeViewModel", "Exception loading upcoming events", e)
            }

            try {
                val trendingResult = getTrendingEventsUseCase(limit = 5).first {
                    it is Resource.Success || it is Resource.Error
                }
                if (trendingResult is Resource.Success) {
                    trendingEvents = trendingResult.data
                    android.util.Log.d(
                        "HomeViewModel",
                        "Trending events loaded: ${trendingEvents.size}"
                    )
                } else if (trendingResult is Resource.Error) {
                    android.util.Log.e(
                        "HomeViewModel",
                        "Error loading trending events: ${trendingResult.error}"
                    )
                }
            } catch (e: Exception) {
                android.util.Log.e("HomeViewModel", "Exception loading trending events", e)
            }

            try {
                val categoriesResult = getCategoriesUseCase().first {
                    it is Resource.Success || it is Resource.Error
                }
                if (categoriesResult is Resource.Success) {
                    categories = categoriesResult.data
                    android.util.Log.d("HomeViewModel", "Categories loaded: ${categories.size}")
                    categories.forEach { cat ->
                        android.util.Log.d(
                            "HomeViewModel",
                            "Category: id=${cat.id}, type=${cat.type}, count=${cat.eventCount}"
                        )
                    }
                } else if (categoriesResult is Resource.Error) {
                    android.util.Log.e(
                        "HomeViewModel",
                        "Error loading categories: ${categoriesResult.error}"
                    )
                }
            } catch (e: Exception) {
                android.util.Log.e("HomeViewModel", "Exception loading categories", e)
            }

            updateState {
                HomeState.Success(
                    user = user ?: User(
                        id = 0,
                        fullName = stringProvider.getString(R.string.guest_user),
                        email = "",
                        role = "USER"
                    ),
                    upcomingEvents = upcomingEvents,
                    trendingEvents = trendingEvents,
                    categories = categories
                )
            }
        }
    }

    private fun navigateToEventDetails(eventId: Int) {
        emitSideEffect(HomeSideEffect.NavigateToEventDetails(eventId))
    }

    private fun navigateToCategory(categoryId: Int) {
        emitSideEffect(HomeSideEffect.NavigateToCategory(categoryId))
    }

    private fun navigateToAllEvents() {
        emitSideEffect(HomeSideEffect.NavigateToAllEvents)
    }

    private fun navigateToNotifications() {
        emitSideEffect(HomeSideEffect.NavigateToNotifications)
        emitSideEffect(HomeSideEffect.ShowErrorMessage(R.string.navigate_to_notifications))
    }

    private fun handleLogout() {
        viewModelScope.launch {
            logoutUseCase()
            emitSideEffect(HomeSideEffect.NavigateToLogin)
        }
    }
}