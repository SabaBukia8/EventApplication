package com.example.eventapplication.presentation.screen.categoryevents

import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import android.os.Bundle
import android.view.View
import com.example.eventapplication.databinding.FragmentCategoryEventsBinding
import com.example.eventapplication.presentation.screen.categoryevents.CategoryEventsFragmentArgs
import com.example.eventapplication.presentation.screen.categoryevents.CategoryEventsFragmentDirections
import com.example.eventapplication.presentation.screen.categoryevents.adapter.NewCategoryEventsAdapter
import com.example.eventapplication.presentation.model.CategoryEventsItem
import com.example.eventapplication.presentation.common.BaseFragment
import com.example.eventapplication.presentation.extensions.gone
import com.example.eventapplication.presentation.extensions.showSnackbar
import com.example.eventapplication.presentation.extensions.toErrorMessage
import com.example.eventapplication.presentation.extensions.visible
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CategoryEventsFragment : BaseFragment<FragmentCategoryEventsBinding>(
    FragmentCategoryEventsBinding::inflate
) {

    private val viewModel: CategoryEventsViewModel by viewModels()
    private lateinit var categoryEventsAdapter: NewCategoryEventsAdapter
    private val args: CategoryEventsFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapter()
        observeState()
        observeSideEffects()

        viewModel.onEvent(CategoryEventsEvent.LoadEvents(args.categoryId))
    }
    
    override fun listeners() {
    }

    private fun setupAdapter() {
        categoryEventsAdapter = NewCategoryEventsAdapter(
            onBackClick = {
                viewModel.onEvent(CategoryEventsEvent.OnBackClicked)
            },
            onNotificationClick = {
                viewModel.onEvent(CategoryEventsEvent.OnNotificationClicked)
            },
            onFilterClick = { filterType ->
                viewModel.onEvent(CategoryEventsEvent.OnFilterChanged(filterType))
            },
            onEventClick = { eventId ->
                viewModel.onEvent(CategoryEventsEvent.OnEventClicked(eventId))
            }
        )

        binding.rvCategoryEvents.apply {
            adapter = categoryEventsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    when (state) {
                        is CategoryEventsState.Idle -> {}
                        is CategoryEventsState.IsLoading -> handleLoading(state.isLoading)
                        is CategoryEventsState.Success -> handleSuccess(state)
                        is CategoryEventsState.Error -> {}
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
                        is CategoryEventsSideEffect.NavigateToEventDetails -> {
                            val action = CategoryEventsFragmentDirections.Companion
                                .actionCategoryEventsFragmentToEventDetailsFragment(effect.eventId)
                            findNavController().navigate(action)
                        }
                        is CategoryEventsSideEffect.NavigateBack -> {
                            findNavController().navigateUp()
                        }
                        is CategoryEventsSideEffect.NavigateToNotifications -> {
                            showSnackbar("Notifications not yet implemented", Snackbar.LENGTH_SHORT)
                        }
                        is CategoryEventsSideEffect.ShowFilterDialog -> {
                            showSnackbar("Filter dialog not yet implemented", Snackbar.LENGTH_SHORT)
                        }
                        is CategoryEventsSideEffect.ShowError -> {
                            val errorMessage = effect.error.toErrorMessage(requireContext())
                            showSnackbar(
                                errorMessage,
                                Snackbar.LENGTH_LONG,
                                "Retry"
                            ) {
                                viewModel.onEvent(CategoryEventsEvent.OnRetry)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun handleLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visible()
            binding.rvCategoryEvents.gone()
            binding.llEmptyState.gone()
        } else {
            binding.progressBar.gone()
        }
    }

    private fun handleSuccess(state: CategoryEventsState.Success) {
        val items = mutableListOf<CategoryEventsItem>()

        items.add(
            CategoryEventsItem.Header(
                categoryName = formatCategoryName(state.category.type?.name),
                hasNotifications = state.hasNotifications,
                selectedFilter = state.selectedFilter
            )
        )

        items.addAll(
            state.events.map { event ->
                CategoryEventsItem.EventCard(
                    event = event,
                    isRegistered = event.registrationStatus != null,
                    isWaitlisted = event.isWaitlisted
                )
            }
        )

        if (state.events.isEmpty()) {
            binding.llEmptyState.visible()
            binding.rvCategoryEvents.gone()
        } else {
            binding.llEmptyState.gone()
            binding.rvCategoryEvents.visible()
            categoryEventsAdapter.submitList(items)
        }
    }

    private fun formatCategoryName(categoryName: String?): String {
        if (categoryName == null) return "Events"

        val formattedName = categoryName.split("_").joinToString(" ") { word ->
            word.lowercase().replaceFirstChar { it.uppercase() }
        }

        return if (formattedName.contains("Events")) formattedName else "$formattedName Events"
    }
}
