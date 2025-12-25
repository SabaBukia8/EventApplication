package com.example.eventapplication.presentation.screen.auth.register

import androidx.lifecycle.viewModelScope
import com.example.eventapplication.R
import com.example.eventapplication.domain.common.Resource
import com.example.eventapplication.domain.model.NetworkError
import com.example.eventapplication.domain.usecase.auth.RegisterUseCase
import com.example.eventapplication.domain.usecase.auth.SendOtpUseCase
import com.example.eventapplication.domain.usecase.auth.VerifyOtpUseCase
import com.example.eventapplication.domain.usecase.department.GetDepartmentsUseCase
import com.example.eventapplication.domain.usecase.validation.ValidateEmailUseCase
import com.example.eventapplication.domain.usecase.validation.ValidateNameUseCase
import com.example.eventapplication.domain.usecase.validation.ValidateOtpUseCase
import com.example.eventapplication.domain.usecase.validation.ValidatePasswordUseCase
import com.example.eventapplication.domain.usecase.validation.ValidatePhoneUseCase
import com.example.eventapplication.domain.validation.ValidationResult
import com.example.eventapplication.presentation.common.BaseViewModel
import com.example.eventapplication.presentation.extensions.toStringResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
    private val getDepartmentsUseCase: GetDepartmentsUseCase,
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase,
    private val validateNameUseCase: ValidateNameUseCase,
    private val validatePhoneUseCase: ValidatePhoneUseCase,
    private val sendOtpUseCase: SendOtpUseCase,
    private val verifyOtpUseCase: VerifyOtpUseCase,
    private val validateOtpUseCase: ValidateOtpUseCase
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

    private val otpDigits = MutableList(6) { "" }
    private var timerJob: Job? = null

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
                val previousPhone = phone
                phone = event.phone
                // Reset verification if phone number changed after verification
                if (previousPhone != event.phone && state.value.isPhoneNumberVerified) {
                    updateState {
                        it.copy(
                            isPhoneNumberVerified = false,
                            otpFlowState = OtpFlowState.Initial,
                            otpTimerSeconds = 0
                        )
                    }
                    otpDigits.fill("")
                    timerJob?.cancel()
                }
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
                handleSendOtp()
            }

            is RegisterEvent.ResendOtpClicked -> {
                handleResendOtp()
            }

            is RegisterEvent.OtpDigitChanged -> {
                handleOtpDigitChanged(event.position, event.digit)
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
        // Check OTP verification FIRST
        if (!state.value.isPhoneNumberVerified) {
            emitSideEffect(RegisterSideEffect.ShowError(R.string.error_phone_not_verified))
            return
        }

        // Validate first name
        val firstNameValidation = validateNameUseCase(firstName)
        if (firstNameValidation is ValidationResult.Error) {
            emitSideEffect(
                RegisterSideEffect.ShowError(
                    firstNameValidation.error.toStringResource(
                        isFirstName = true
                    )
                )
            )
            return
        }

        // Validate last name
        val lastNameValidation = validateNameUseCase(lastName)
        if (lastNameValidation is ValidationResult.Error) {
            emitSideEffect(
                RegisterSideEffect.ShowError(
                    lastNameValidation.error.toStringResource(
                        isFirstName = false
                    )
                )
            )
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

    private fun handleSendOtp() {
        // Validate phone number first
        val phoneValidation = validatePhoneUseCase(phone)
        if (phoneValidation is ValidationResult.Error) {
            emitSideEffect(RegisterSideEffect.ShowError(phoneValidation.error.toStringResource()))
            return
        }

        // Send OTP
        viewModelScope.launch {
            sendOtpUseCase(phone).collect { resource ->
                when (resource) {
                    is Resource.Loader -> {
                        if (resource.isLoading) {
                            updateState { it.copy(otpFlowState = OtpFlowState.OtpSending) }
                        }
                    }

                    is Resource.Success -> {
                        updateState {
                            it.copy(
                                otpFlowState = OtpFlowState.OtpSent,
                                otpTimerSeconds = resource.data.expirySeconds
                            )
                        }
                        startOtpTimer(resource.data.expirySeconds)
                        emitSideEffect(RegisterSideEffect.ShowMessageString(resource.data.message))
                        emitSideEffect(RegisterSideEffect.FocusOtpField)
                    }

                    is Resource.Error -> {
                        updateState { it.copy(otpFlowState = OtpFlowState.Initial) }
                        handleOtpError(resource.error)
                    }
                }
            }
        }
    }

    private fun handleResendOtp() {
        // Clear previous OTP
        otpDigits.fill("")
        emitSideEffect(RegisterSideEffect.ClearOtpFields)

        // Resend OTP
        handleSendOtp()
    }

    private fun handleOtpDigitChanged(position: Int, digit: String) {
        if (position !in 0..5) return

        otpDigits[position] = digit

        // Auto-advance to next field if digit entered
        if (digit.isNotEmpty() && position < 5) {
            emitSideEffect(RegisterSideEffect.MoveFocusToOtpPosition(position + 1))
        }

        // Auto-verify when all 6 digits are entered
        if (otpDigits.all { it.isNotEmpty() }) {
            val fullOtp = otpDigits.joinToString("")
            handleVerifyOtp(fullOtp)
        }
    }

    private fun handleVerifyOtp(otp: String) {
        // Validate OTP format
        val otpValidation = validateOtpUseCase(otp)
        if (otpValidation is ValidationResult.Error) {
            emitSideEffect(RegisterSideEffect.ShowError(otpValidation.error.toStringResource()))
            return
        }

        // Verify OTP
        viewModelScope.launch {
            verifyOtpUseCase(phone, otp).collect { resource ->
                when (resource) {
                    is Resource.Loader -> {
                        if (resource.isLoading) {
                            updateState { it.copy(otpFlowState = OtpFlowState.OtpVerifying) }
                        }
                    }

                    is Resource.Success -> {
                        if (resource.data.isVerified) {
                            updateState {
                                it.copy(
                                    otpFlowState = OtpFlowState.OtpVerified,
                                    isPhoneNumberVerified = true
                                )
                            }
                            timerJob?.cancel()
                            emitSideEffect(RegisterSideEffect.ShowMessageString(resource.data.message))
                        } else {
                            updateState { it.copy(otpFlowState = OtpFlowState.OtpSent) }
                            emitSideEffect(RegisterSideEffect.ShowError(R.string.error_otp_invalid))
                        }
                    }

                    is Resource.Error -> {
                        updateState { it.copy(otpFlowState = OtpFlowState.OtpSent) }
                        handleOtpError(resource.error)
                    }
                }
            }
        }
    }

    private fun startOtpTimer(seconds: Int) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            var remainingSeconds = seconds
            while (remainingSeconds > 0) {
                delay(1000)
                remainingSeconds--
                updateState { it.copy(otpTimerSeconds = remainingSeconds) }
            }
            // Timer expired
            updateState { it.copy(otpFlowState = OtpFlowState.OtpExpired) }
        }
    }

    private fun handleOtpError(error: NetworkError) {
        val errorMessageResId = when (error) {
            is NetworkError.Unknown -> R.string.error_unknown
            NetworkError.NoInternet -> R.string.error_no_internet
            NetworkError.Timeout -> R.string.error_timeout
            NetworkError.ServerError -> R.string.error_server_error
            else -> R.string.operation_failed
        }
        emitSideEffect(RegisterSideEffect.ShowError(errorMessageResId))
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
