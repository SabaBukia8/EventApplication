package com.example.eventapplication.presentation.screen.auth.splash

import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.eventapplication.databinding.FragmentSplashBinding
import com.example.eventapplication.presentation.screen.auth.splash.SplashFragmentDirections
import com.example.eventapplication.presentation.common.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashFragment : BaseFragment<FragmentSplashBinding>(
    FragmentSplashBinding::inflate
) {

    private val viewModel: SplashViewModel by viewModels()

    override fun bind() {
        observeSideEffects()
    }

    private fun observeSideEffects() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.sideEffect.collect { sideEffect ->
                    when (sideEffect) {
                        is SplashSideEffect.NavigateToLogin -> {
                            findNavController().navigate(
                                SplashFragmentDirections.Companion.actionSplashFragmentToLoginFragment()
                            )
                        }
                        is SplashSideEffect.NavigateToHome -> {
                            findNavController().navigate(
                                SplashFragmentDirections.Companion.actionSplashFragmentToHomeFragment()
                            )
                        }
                    }
                }
            }
        }
    }
}
