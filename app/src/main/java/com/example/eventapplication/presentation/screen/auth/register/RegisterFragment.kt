package com.example.eventapplication.presentation.screen.auth.register

import android.view.View
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.eventapplication.R
import com.example.eventapplication.databinding.FragmentRegisterBinding
import com.example.eventapplication.presentation.screen.auth.register.RegisterFragmentDirections
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

    override fun bind() {
        observeState()
        observeSideEffects()
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
                    }
                }
            }
        }
    }
}
