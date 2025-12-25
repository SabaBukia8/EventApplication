package com.example.eventapplication.presentation.screen.home

import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.eventapplication.databinding.FragmentHomeBinding
import com.example.eventapplication.presentation.common.BaseFragment
import com.example.eventapplication.presentation.extensions.showSnackbar
import com.example.eventapplication.presentation.extensions.toErrorMessage
import com.example.eventapplication.presentation.model.HomeItem
import com.example.eventapplication.presentation.screen.home.adapter.HomeAdapter
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(
    FragmentHomeBinding::inflate
) {

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var homeAdapter: HomeAdapter

    override fun listeners() {
        setupAdapter()
        observeState()
        observeSideEffects()

        viewModel.onEvent(HomeEvent.LoadData)
    }

    private fun setupAdapter() {
        homeAdapter = HomeAdapter(
            onEventClick = { eventId ->
                viewModel.onEvent(HomeEvent.OnEventClicked(eventId))
            },
            onCategoryClick = { categoryId ->
                viewModel.onEvent(HomeEvent.OnCategoryClicked(categoryId))
            },
            onViewAllClick = {
                viewModel.onEvent(HomeEvent.OnViewAllEventsClicked)
            },
            onNotificationClick = {
                viewModel.onEvent(HomeEvent.OnNotificationClicked)
            },
            onProfileClick = {
                viewModel.onEvent(HomeEvent.OnLogoutClicked)
            }
        )

        binding.rvHome.adapter = homeAdapter
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    when (state) {
                        is HomeState.IsLoading -> handleLoading()
                        is HomeState.Success -> buildHomeItems(state)
                        is HomeState.Error -> {}
                        is HomeState.Idle -> {}
                    }
                }
            }
        }
    }

    private fun observeSideEffects() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.sideEffect.collect { effect ->
                    when (effect) {
                        is HomeSideEffect.NavigateToEventDetails -> {
                            val action = HomeFragmentDirections.Companion
                                .actionHomeFragmentToEventDetailsFragment(effect.eventId)
                            findNavController().navigate(action)
                        }

                        is HomeSideEffect.NavigateToCategory -> {
                            try {
                                val action = HomeFragmentDirections.Companion
                                    .actionHomeFragmentToCategoryEventsFragment(effect.categoryId)
                                findNavController().navigate(action)
                            } catch (e: Exception) {
                                val errorMsg = "Error navigating to category: ${e.message}"
                                showSnackbar(errorMsg, Snackbar.LENGTH_LONG)
                            }
                        }

                        is HomeSideEffect.NavigateToAllEvents -> {
                            val action = HomeFragmentDirections.Companion
                                .actionHomeFragmentToBrowseFragment(-1)
                            findNavController().navigate(action)
                        }

                        is HomeSideEffect.NavigateToNotifications -> {
                        }

                        is HomeSideEffect.NavigateToLogin -> {
                            val action = HomeFragmentDirections.actionHomeFragmentToLoginFragment()
                            findNavController().navigate(action)
                        }

                        is HomeSideEffect.ShowErrorMessage -> {
                            if (effect.formatArgs.isEmpty()) {
                                showSnackbar(getString(effect.messageResId), Snackbar.LENGTH_SHORT)
                            } else {
                                showSnackbar(
                                    getString(effect.messageResId, *effect.formatArgs),
                                    Snackbar.LENGTH_SHORT
                                )
                            }
                        }

                        is HomeSideEffect.ShowError -> {
                            val errorMessage = effect.error.toErrorMessage(requireContext())
                            showSnackbar(errorMessage, Snackbar.LENGTH_LONG)
                        }
                    }
                }
            }
        }
    }

    private fun handleLoading() {
    }

    private fun buildHomeItems(state: HomeState.Success) {
        val homeItems = listOf(
            HomeItem.Header(
                userName = state.user.fullName,
                hasNotifications = true
            ),
            HomeItem.Welcome(state.user.fullName),
            HomeItem.UpcomingEventsSection(state.upcomingEvents),
            HomeItem.CategoriesSection(state.categories),
            HomeItem.TrendingEventsSection(state.trendingEvents)
        )
        homeAdapter.submitList(homeItems)
    }
}
