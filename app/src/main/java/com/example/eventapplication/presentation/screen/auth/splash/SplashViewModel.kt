package com.example.eventapplication.presentation.screen.auth.splash

import androidx.lifecycle.viewModelScope
import com.example.eventapplication.domain.usecase.auth.IsUserLoggedInUseCase
import com.example.eventapplication.presentation.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val isUserLoggedInUseCase: IsUserLoggedInUseCase
) : BaseViewModel<SplashState, SplashEvent, SplashSideEffect>(SplashState()) {

    init {
        checkAuthStatus()
    }

    override fun onEvent(event: SplashEvent) {
        when (event) {
            is SplashEvent.CheckAuthStatus -> checkAuthStatus()
        }
    }

    private fun checkAuthStatus() {
        viewModelScope.launch {
            delay(1500)

            // Check if user has a valid token (session exists)
            val isLoggedIn = isUserLoggedInUseCase().first()

            if (isLoggedIn) {
                emitSideEffect(SplashSideEffect.NavigateToHome)
            } else {
                emitSideEffect(SplashSideEffect.NavigateToLogin)
            }
        }
    }
}
