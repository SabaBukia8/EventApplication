package com.example.eventapplication.presentation.screen.auth.login

import androidx.lifecycle.viewModelScope
import com.example.eventapplication.R
import com.example.eventapplication.domain.usecase.auth.GetRememberedEmailUseCase
import com.example.eventapplication.domain.usecase.auth.LoginUseCase
import com.example.eventapplication.domain.usecase.auth.SaveRememberMeUseCase
import com.example.eventapplication.domain.usecase.validation.ValidateEmailUseCase
import com.example.eventapplication.domain.usecase.validation.ValidatePasswordUseCase
import com.example.eventapplication.domain.common.Resource
import com.example.eventapplication.domain.model.NetworkError
import com.example.eventapplication.domain.validation.ValidationResult
import com.example.eventapplication.presentation.common.BaseViewModel
import com.example.eventapplication.presentation.extensions.toStringResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay // <--- IMPORT THIS
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase,
    private val saveRememberMeUseCase: SaveRememberMeUseCase,
    private val getRememberedEmailUseCase: GetRememberedEmailUseCase
) : BaseViewModel<LoginState, LoginEvent, LoginSideEffect>(LoginState()) {

    private var email = ""
    private var password = ""
    private var rememberMe = false

    init {
        loadRememberedCredentials()
    }

    private fun loadRememberedCredentials() {
        viewModelScope.launch {
            val (isRemembered, rememberedEmail) = getRememberedEmailUseCase()
            if (isRemembered) {
                email = rememberedEmail
                rememberMe = true
                updateState { it.copy(rememberMe = true, email = rememberedEmail) }
            }
        }
    }

    override fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.EmailChanged -> {
                email = event.email
                updateState { it.copy(email = event.email) }
            }
            is LoginEvent.PasswordChanged -> {
                password = event.password
            }
            is LoginEvent.RememberMeChanged -> {
                rememberMe = event.rememberMe
                updateState { it.copy(rememberMe = event.rememberMe) }
            }
            is LoginEvent.LoginClicked -> {
                handleLogin()
            }
            is LoginEvent.NavigateToRegisterClicked -> {
                emitSideEffect(LoginSideEffect.NavigateToRegister)
            }
            is LoginEvent.ForgotPasswordClicked -> {
                emitSideEffect(LoginSideEffect.ShowError(R.string.forgot_password_coming_soon))
            }
        }
    }

    private fun handleLogin() {
        val emailValidation = validateEmailUseCase(email)
        if (emailValidation is ValidationResult.Error) {
            emitSideEffect(LoginSideEffect.ShowError(emailValidation.error.toStringResource()))
            return
        }

        val passwordValidation = validatePasswordUseCase(password)
        if (passwordValidation is ValidationResult.Error) {
            emitSideEffect(LoginSideEffect.ShowError(passwordValidation.error.toStringResource()))
            return
        }

        viewModelScope.launch {
            // Check your LoginUseCase definition. Usually it takes (email, password).
            // If yours takes (email, password, rememberMe), keep it as is.
            loginUseCase(email, password).collect { resource ->
                when (resource) {
                    is Resource.Loader -> {
                        updateState { it.copy(isLoading = resource.isLoading) }
                    }
                    is Resource.Success -> {
                        updateState { it.copy(isLoading = false) }

                        // 1. Save Token and User Data
                        // We use the local 'email' variable which holds the user input
                        loginUseCase.saveToken(resource.data.token)
                        loginUseCase.saveUserData(resource.data, email)

                        // 2. Handle "Remember Me" for next app launch auto-fill
                        if (rememberMe) {
                            saveRememberMeUseCase(true, email)
                        } else {
                            saveRememberMeUseCase(false, "")
                        }

                        // 3. CRITICAL FIX: Wait for DataStore to finish writing to disk
                        // Without this, the Home Screen loads before the name is saved.
                        delay(500)

                        emitSideEffect(LoginSideEffect.NavigateToHome)
                    }
                    is Resource.Error -> {
                        updateState { it.copy(isLoading = false) }
                        val errorMessageResId = when (resource.error) {
                            is NetworkError.Unknown -> (resource.error as NetworkError.Unknown).message?.let { R.string.unknown_error } ?: R.string.login_failed
                            NetworkError.Unauthorized -> R.string.invalid_credentials
                            NetworkError.NoInternet -> R.string.error_no_internet
                            NetworkError.Forbidden -> R.string.login_failed
                            NetworkError.NotFound -> R.string.login_failed
                            NetworkError.ServerError -> R.string.error_server_error
                            NetworkError.Timeout -> R.string.error_timeout
                            else -> R.string.login_failed
                        }
                        emitSideEffect(LoginSideEffect.ShowError(errorMessageResId))
                    }
                }
            }
        }
    }
}