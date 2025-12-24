package com.example.eventapplication.presentation.screen.auth.login

data class LoginState(
    val isLoading: Boolean = false,
    val rememberMe: Boolean = false,
    val email: String = ""
)

sealed class LoginEvent {
    data class EmailChanged(val email: String) : LoginEvent()
    data class PasswordChanged(val password: String) : LoginEvent()
    data class RememberMeChanged(val rememberMe: Boolean) : LoginEvent()
    data object LoginClicked : LoginEvent()
    data object NavigateToRegisterClicked : LoginEvent()
    data object ForgotPasswordClicked : LoginEvent()
}

sealed class LoginSideEffect {
    data object NavigateToHome : LoginSideEffect()
    data object NavigateToRegister : LoginSideEffect()
    data object NavigateToForgotPassword : LoginSideEffect()
    data class ShowError(val messageResId: Int) : LoginSideEffect()
}
