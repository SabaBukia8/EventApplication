package com.example.eventapplication.presentation.screen.auth.register

import com.example.eventapplication.domain.model.Department

data class RegisterState(
    val isLoading: Boolean = false,
    val selectedDepartment: String = "",
    val termsAccepted: Boolean = false,
    val departments: List<Department> = emptyList()
)

sealed class RegisterEvent {
    data class FirstNameChanged(val firstName: String) : RegisterEvent()
    data class LastNameChanged(val lastName: String) : RegisterEvent()
    data class EmailChanged(val email: String) : RegisterEvent()
    data class PhoneChanged(val phone: String) : RegisterEvent()
    data class DepartmentSelected(val department: String) : RegisterEvent()
    data class PasswordChanged(val password: String) : RegisterEvent()
    data class ConfirmPasswordChanged(val confirmPassword: String) : RegisterEvent()
    data class TermsAcceptedChanged(val accepted: Boolean) : RegisterEvent()
    data object SendOtpClicked : RegisterEvent()
    data class OtpChanged(val otp: String) : RegisterEvent()
    data object RegisterClicked : RegisterEvent()
    data object NavigateToLoginClicked : RegisterEvent()
}

sealed class RegisterSideEffect {
    data object NavigateToHome : RegisterSideEffect()
    data object NavigateToLogin : RegisterSideEffect()
    // Use string resource IDs instead of direct strings
    data class ShowError(val messageResId: Int) : RegisterSideEffect()
    data class ShowErrorString(val message: String) : RegisterSideEffect() // For legacy/fallback cases
    data class ShowMessage(val messageResId: Int) : RegisterSideEffect()
    data class ShowMessageString(val message: String) : RegisterSideEffect() // For legacy/fallback cases
}
