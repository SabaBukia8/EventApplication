package com.example.eventapplication.presentation.screen.auth.register

import androidx.lifecycle.viewModelScope
import com.example.eventapplication.R
import com.example.eventapplication.domain.usecase.auth.RegisterUseCase
import com.example.eventapplication.domain.usecase.department.GetDepartmentsUseCase
import com.example.eventapplication.domain.usecase.validation.ValidateEmailUseCase
import com.example.eventapplication.domain.usecase.validation.ValidateNameUseCase
import com.example.eventapplication.domain.usecase.validation.ValidatePasswordUseCase
import com.example.eventapplication.domain.usecase.validation.ValidatePhoneUseCase
import com.example.eventapplication.domain.common.Resource
import com.example.eventapplication.domain.model.NetworkError
import com.example.eventapplication.domain.validation.ValidationResult
import com.example.eventapplication.presentation.common.BaseViewModel
import com.example.eventapplication.presentation.extensions.toStringResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
    private val getDepartmentsUseCase: GetDepartmentsUseCase,
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase,
    private val validateNameUseCase: ValidateNameUseCase,
    private val validatePhoneUseCase: ValidatePhoneUseCase
) : BaseViewModel<RegisterState, RegisterEvent, RegisterSideEffect>(RegisterState()) {

    private var firstName = ""
    private var lastName = ""
    private var email = ""
    private var phone = ""
    private var department = ""
    private var selectedDepartmentId: Int? = null
    private var password = ""
    private var confirmPassword = ""
    private var otp = ""
    private var termsAccepted = false

    init {
        loadDepartments()
    }

    private fun loadDepartments() {
        viewModelScope.launch {
            getDepartmentsUseCase().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        updateState { it.copy(departments = resource.data) }
                    }
                    is Resource.Error -> {
                        emitSideEffect(RegisterSideEffect.ShowError(R.string.failed_to_load_departments))
                    }
                    is Resource.Loader -> {
                    }
                }
            }
        }
    }

    override fun onEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.FirstNameChanged -> {
                firstName = event.firstName
            }
            is RegisterEvent.LastNameChanged -> {
                lastName = event.lastName
            }
            is RegisterEvent.EmailChanged -> {
                email = event.email
            }
            is RegisterEvent.PhoneChanged -> {
                phone = event.phone
            }
            is RegisterEvent.DepartmentSelected -> {
                department = event.department
                val dept = state.value.departments.find { it.name == event.department }
                selectedDepartmentId = dept?.id
                updateState { it.copy(selectedDepartment = event.department) }
            }
            is RegisterEvent.PasswordChanged -> {
                password = event.password
            }
            is RegisterEvent.ConfirmPasswordChanged -> {
                confirmPassword = event.confirmPassword
            }
            is RegisterEvent.TermsAcceptedChanged -> {
                termsAccepted = event.accepted
                updateState { it.copy(termsAccepted = event.accepted) }
            }
            is RegisterEvent.SendOtpClicked -> {
                emitSideEffect(RegisterSideEffect.ShowMessage(R.string.otp_feature_disabled))
            }
            is RegisterEvent.OtpChanged -> {
                otp = event.otp
            }
            is RegisterEvent.RegisterClicked -> {
                handleRegister()
            }
            is RegisterEvent.NavigateToLoginClicked -> {
                emitSideEffect(RegisterSideEffect.NavigateToLogin)
            }
        }
    }

    private fun handleRegister() {
        // Validate first name
        val firstNameValidation = validateNameUseCase(firstName)
        if (firstNameValidation is ValidationResult.Error) {
            emitSideEffect(RegisterSideEffect.ShowError(firstNameValidation.error.toStringResource(isFirstName = true)))
            return
        }

        // Validate last name
        val lastNameValidation = validateNameUseCase(lastName)
        if (lastNameValidation is ValidationResult.Error) {
            emitSideEffect(RegisterSideEffect.ShowError(lastNameValidation.error.toStringResource(isFirstName = false)))
            return
        }

        // Validate email
        val emailValidation = validateEmailUseCase(email)
        if (emailValidation is ValidationResult.Error) {
            emitSideEffect(RegisterSideEffect.ShowError(emailValidation.error.toStringResource()))
            return
        }

        // Validate phone
        val phoneValidation = validatePhoneUseCase(phone)
        if (phoneValidation is ValidationResult.Error) {
            emitSideEffect(RegisterSideEffect.ShowError(phoneValidation.error.toStringResource()))
            return
        }

        // Validate department
        if (selectedDepartmentId == null) {
            emitSideEffect(RegisterSideEffect.ShowError(R.string.error_department_required))
            return
        }

        // Validate password
        val passwordValidation = validatePasswordUseCase(password)
        if (passwordValidation is ValidationResult.Error) {
            emitSideEffect(RegisterSideEffect.ShowError(passwordValidation.error.toStringResource()))
            return
        }

        // Validate password confirmation
        if (password != confirmPassword) {
            emitSideEffect(RegisterSideEffect.ShowError(R.string.passwords_do_not_match))
            return
        }

        // Validate terms acceptance
        if (!termsAccepted) {
            emitSideEffect(RegisterSideEffect.ShowError(R.string.error_terms_required))
            return
        }

        // Perform registration
        viewModelScope.launch {
            registerUseCase(
                email = email,
                password = password,
                firstName = firstName,
                lastName = lastName,
                mobileNumber = phone,
                departmentId = selectedDepartmentId!!,
                confirmPassword = confirmPassword
            ).collect { resource ->
                when (resource) {
                    is Resource.Loader -> {
                        updateState { it.copy(isLoading = resource.isLoading) }
                    }
                    is Resource.Success -> {
                        updateState { it.copy(isLoading = false) }
                        // If we get a success message from the server, use it, otherwise use our default
                        if (resource.data.message.isNotEmpty()) {
                            emitSideEffect(RegisterSideEffect.ShowMessageString(resource.data.message))
                        } else {
                            emitSideEffect(RegisterSideEffect.ShowMessage(R.string.registration_successful))
                        }
                        emitSideEffect(RegisterSideEffect.NavigateToLogin)
                    }
                    is Resource.Error -> {
                        updateState { it.copy(isLoading = false) }
                        // If we get a specific error message from the backend, use it
                        if (resource.error is NetworkError.Unknown && !resource.error.message.isNullOrEmpty()) {
                            emitSideEffect(RegisterSideEffect.ShowErrorString(resource.error.message))
                        } else {
                            emitSideEffect(RegisterSideEffect.ShowError(R.string.registration_failed))
                        }
                    }
                }
            }
        }
    }
}
