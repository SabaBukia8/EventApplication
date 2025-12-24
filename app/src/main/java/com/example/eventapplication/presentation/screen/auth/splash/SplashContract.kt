package com.example.eventapplication.presentation.screen.auth.splash

data class SplashState(
    val isLoading: Boolean = true
)

sealed class SplashEvent {
    data object CheckAuthStatus : SplashEvent()
}

sealed class SplashSideEffect {
    data object NavigateToLogin : SplashSideEffect()
    data object NavigateToHome : SplashSideEffect()
}
