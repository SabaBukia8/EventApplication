package com.example.eventapplication.presentation.screen.auth.login

import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.eventapplication.R
import com.example.eventapplication.databinding.FragmentLoginBinding
import com.example.eventapplication.presentation.common.BaseFragment
import com.example.eventapplication.presentation.extensions.showSnackbar
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(
    FragmentLoginBinding::inflate
) {

    private val viewModel: LoginViewModel by viewModels()

    override fun bind() {
        observeState()
        observeSideEffects()
    }

    override fun listeners() {
        binding.apply {
            etEmail.addTextChangedListener { text ->
                viewModel.onEvent(LoginEvent.EmailChanged(text.toString()))
            }

            etPassword.addTextChangedListener { text ->
                viewModel.onEvent(LoginEvent.PasswordChanged(text.toString()))
            }

            cbRememberMe.setOnCheckedChangeListener { _, isChecked ->
                viewModel.onEvent(LoginEvent.RememberMeChanged(isChecked))
            }

            btnLogin.setOnClickListener {
                viewModel.onEvent(LoginEvent.LoginClicked)
            }

            tvRegister.setOnClickListener {
                viewModel.onEvent(LoginEvent.NavigateToRegisterClicked)
            }

            tvForgotPassword.setOnClickListener {
                viewModel.onEvent(LoginEvent.ForgotPasswordClicked)
            }
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    binding.apply {
                        btnLogin.isEnabled = !state.isLoading
                        progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE

                        if (state.email.isNotEmpty() && etEmail.text.isNullOrEmpty()) {
                            etEmail.setText(state.email)
                        }

                        if (cbRememberMe.isChecked != state.rememberMe) {
                            cbRememberMe.isChecked = state.rememberMe
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
                        is LoginSideEffect.NavigateToHome -> {
                            findNavController().navigate(
                                LoginFragmentDirections.Companion.actionLoginFragmentToHomeFragment()
                            )
                        }

                        is LoginSideEffect.NavigateToRegister -> {
                            findNavController().navigate(
                                LoginFragmentDirections.Companion.actionLoginFragmentToRegisterFragment()
                            )
                        }

                        is LoginSideEffect.NavigateToForgotPassword -> {
                            showSnackbar(
                                getString(R.string.forgot_password_coming_soon),
                                Snackbar.LENGTH_LONG
                            )
                        }

                        is LoginSideEffect.ShowError -> {
                            showSnackbar(getString(sideEffect.messageResId), Snackbar.LENGTH_LONG)
                        }
                    }
                }
            }
        }
    }
}
