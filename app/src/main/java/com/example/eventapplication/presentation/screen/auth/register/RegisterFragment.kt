package com.example.eventapplication.presentation.screen.auth.register

import android.content.Context
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.eventapplication.R
import com.example.eventapplication.databinding.FragmentRegisterBinding
import com.example.eventapplication.presentation.common.BaseFragment
import com.example.eventapplication.presentation.extensions.showSnackbar
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment : BaseFragment<FragmentRegisterBinding>(
    FragmentRegisterBinding::inflate
) {

    private val viewModel: RegisterViewModel by viewModels()
    private lateinit var otpInputs: List<AppCompatEditText>

    override fun bind() {
        setupOtpInputs()
        observeState()
        observeSideEffects()
    }

    private fun setupOtpInputs() {
        binding.apply {
            otpInputs = listOf(etOtp1, etOtp2, etOtp3, etOtp4, etOtp5, etOtp6)

            otpInputs.forEachIndexed { index, editText ->
                // Text watcher for digit changes
                editText.addTextChangedListener { text ->
                    val digit = text?.toString() ?: ""
                    // Only trigger event if length is 0 or 1
                    if (digit.length <= 1) {
                        viewModel.onEvent(RegisterEvent.OtpDigitChanged(index, digit))
                    } else {
                        // If more than 1 character, keep only the first
                        editText.setText(digit.take(1))
                        editText.setSelection(1)
                    }
                }

                // Backspace handling
                editText.setOnKeyListener { _, keyCode, event ->
                    if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                        if (editText.text.isNullOrEmpty() && index > 0) {
                            otpInputs[index - 1].requestFocus()
                            return@setOnKeyListener true
                        }
                    }
                    false
                }
            }
        }
    }

    override fun listeners() {
        binding.apply {
            etFirstName.addTextChangedListener { text ->
                viewModel.onEvent(RegisterEvent.FirstNameChanged(text.toString()))
            }

            etLastName.addTextChangedListener { text ->
                viewModel.onEvent(RegisterEvent.LastNameChanged(text.toString()))
            }

            etEmail.addTextChangedListener { text ->
                viewModel.onEvent(RegisterEvent.EmailChanged(text.toString()))
            }

            etPhone.addTextChangedListener { text ->
                viewModel.onEvent(RegisterEvent.PhoneChanged(text.toString()))
            }

            actvDepartment.setOnItemClickListener { _, _, _, _ ->
                val selectedDepartment = actvDepartment.text.toString()
                viewModel.onEvent(RegisterEvent.DepartmentSelected(selectedDepartment))
            }

            etPassword.addTextChangedListener { text ->
                viewModel.onEvent(RegisterEvent.PasswordChanged(text.toString()))
            }

            etConfirmPassword.addTextChangedListener { text ->
                viewModel.onEvent(RegisterEvent.ConfirmPasswordChanged(text.toString()))
            }

            cbTerms.setOnCheckedChangeListener { _, isChecked ->
                viewModel.onEvent(RegisterEvent.TermsAcceptedChanged(isChecked))
            }

            btnSendOtp.setOnClickListener {
                viewModel.onEvent(RegisterEvent.SendOtpClicked)
            }

            btnRegister.setOnClickListener {
                viewModel.onEvent(RegisterEvent.RegisterClicked)
            }

            tvLogin.setOnClickListener {
                viewModel.onEvent(RegisterEvent.NavigateToLoginClicked)
            }
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    binding.apply {
                        btnRegister.isEnabled = !state.isLoading
                        progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE

                        if (state.departments.isNotEmpty()) {
                            val departmentNames = state.departments.map { it.name }
                            val adapter = ArrayAdapter(
                                requireContext(),
                                android.R.layout.simple_dropdown_item_1line,
                                departmentNames
                            )
                            actvDepartment.setAdapter(adapter)
                        }

                        if (state.selectedDepartment.isNotEmpty() && actvDepartment.text.isNullOrEmpty()) {
                            actvDepartment.setText(state.selectedDepartment, false)
                        }
                        if (cbTerms.isChecked != state.termsAccepted) {
                            cbTerms.isChecked = state.termsAccepted
                        }

                        // Handle OTP flow state
                        handleOtpFlowState(state.otpFlowState, state.otpTimerSeconds)
                    }
                }
            }
        }
    }

    private fun observeSideEffects() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.sideEffect.collect { sideEffect ->
                    when (sideEffect) {
                        is RegisterSideEffect.NavigateToHome -> {
                            findNavController().navigate(
                                RegisterFragmentDirections.Companion.actionRegisterFragmentToHomeFragment()
                            )
                        }

                        is RegisterSideEffect.NavigateToLogin -> {
                            findNavController().popBackStack()
                        }

                        is RegisterSideEffect.ShowError -> {
                            showSnackbar(sideEffect.messageResId, Snackbar.LENGTH_LONG)
                        }

                        is RegisterSideEffect.ShowErrorString -> {
                            showSnackbar(sideEffect.message, Snackbar.LENGTH_LONG)
                        }

                        is RegisterSideEffect.ShowMessage -> {
                            showSnackbar(sideEffect.messageResId, Snackbar.LENGTH_SHORT)
                        }

                        is RegisterSideEffect.ShowMessageString -> {
                            showSnackbar(sideEffect.message, Snackbar.LENGTH_SHORT)
                        }

                        is RegisterSideEffect.FocusOtpField -> {
                            otpInputs.first().requestFocus()
                            val imm =
                                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            imm.showSoftInput(otpInputs.first(), InputMethodManager.SHOW_IMPLICIT)
                        }

                        is RegisterSideEffect.MoveFocusToOtpPosition -> {
                            if (sideEffect.position in otpInputs.indices) {
                                otpInputs[sideEffect.position].requestFocus()
                            }
                        }

                        is RegisterSideEffect.ClearOtpFields -> {
                            otpInputs.forEach { it.text?.clear() }
                            otpInputs.first().requestFocus()
                        }
                    }
                }
            }
        }
    }

    private fun handleOtpFlowState(flowState: OtpFlowState, timerSeconds: Int) {
        binding.apply {
            when (flowState) {
                OtpFlowState.Initial -> {
                    etPhone.isEnabled = true
                    btnSendOtp.isEnabled = true
                    btnSendOtp.text = getString(R.string.send_otp_button)
                    llOtpSection.alpha = 0.5f
                    otpInputs.forEach { it.isEnabled = false }
                    tvCodeExpires.visibility = View.GONE
                }

                OtpFlowState.OtpSending -> {
                    btnSendOtp.isEnabled = false
                }

                OtpFlowState.OtpSent -> {
                    etPhone.isEnabled = false
                    btnSendOtp.isEnabled = false
                    llOtpSection.alpha = 1.0f
                    otpInputs.forEach { it.isEnabled = true }
                    tvCodeExpires.visibility = View.VISIBLE
                    updateTimerDisplay(timerSeconds)
                }

                OtpFlowState.OtpVerifying -> {
                    otpInputs.forEach { it.isEnabled = false }
                }

                OtpFlowState.OtpVerified -> {
                    etPhone.isEnabled = false
                    llOtpSection.alpha = 0.5f
                    otpInputs.forEach { it.isEnabled = false }
                    tvCodeExpires.visibility = View.GONE
                    btnSendOtp.visibility = View.GONE
                }

                OtpFlowState.OtpExpired -> {
                    btnSendOtp.isEnabled = true
                    btnSendOtp.text = getString(R.string.resend_otp_button)
                    btnSendOtp.visibility = View.VISIBLE
                    tvCodeExpires.text = getString(R.string.otp_expired)
                    otpInputs.forEach { it.isEnabled = false }
                }
            }
        }
    }

    private fun updateTimerDisplay(seconds: Int) {
        val minutes = seconds / 60
        val secs = seconds % 60
        binding.tvCodeExpires.text = getString(
            R.string.code_expires_in,
            String.format("%02d:%02d", minutes, secs)
        )
    }
}
