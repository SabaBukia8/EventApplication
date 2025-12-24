package com.example.eventapplication.presentation.screen.browse

import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eventapplication.R
import com.example.eventapplication.databinding.FragmentBrowseEventsBinding
import com.example.eventapplication.presentation.screen.browse.BrowseFragmentArgs
import com.example.eventapplication.presentation.screen.browse.BrowseFragmentDirections
import com.example.eventapplication.presentation.screen.browse.adapter.BrowseAdapter
import com.example.eventapplication.presentation.model.BrowseItem
import com.example.eventapplication.presentation.common.BaseFragment
import com.example.eventapplication.presentation.extensions.gone
import com.example.eventapplication.presentation.extensions.showSnackbar
import com.example.eventapplication.presentation.extensions.toErrorMessage
import com.example.eventapplication.presentation.extensions.visible
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BrowseFragment : BaseFragment<FragmentBrowseEventsBinding>(
    FragmentBrowseEventsBinding::inflate
) {

    private val viewModel: BrowseViewModel by viewModels()
    private lateinit var browseAdapter: BrowseAdapter
    private val args: BrowseFragmentArgs by navArgs()
    private var isInitialCategorySet = false

    override fun listeners() {
        setupAdapter()
        observeState()
        observeSideEffects()

        viewModel.onEvent(BrowseEvent.LoadData)
    }

    private fun setupAdapter() {
        browseAdapter = BrowseAdapter(
            onSearchQueryChanged = { query ->
                viewModel.onEvent(BrowseEvent.OnSearchQueryChanged(query))
            },
            onFilterClick = {
                viewModel.onEvent(BrowseEvent.OnFilterClicked)
            },
            onCategoryClick = { categoryId ->
                viewModel.onEvent(BrowseEvent.OnCategorySelected(categoryId))
            },
            onEventClick = { eventId ->
                viewModel.onEvent(BrowseEvent.OnEventClicked(eventId))
            },
            onClearFilters = {
                viewModel.onEvent(BrowseEvent.OnClearFilters)
            }
        )

        binding.rvBrowse.apply {
            adapter = browseAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    when (state) {
                        is BrowseState.Idle -> {}
                        is BrowseState.IsLoading -> handleLoading(state.isLoading)
                        is BrowseState.Success -> {
                            buildBrowseItems(state)

                            if (!isInitialCategorySet && args.categoryId != -1) {
                                isInitialCategorySet = true
                                viewModel.onEvent(BrowseEvent.OnCategorySelected(args.categoryId))
                            }
                        }
                        is BrowseState.Error -> {}
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
                        is BrowseSideEffect.NavigateToEventDetails -> {
                            val action = BrowseFragmentDirections.Companion
                                .actionBrowseFragmentToEventDetailsFragment(effect.eventId)
                            findNavController().navigate(action)
                        }
                        is BrowseSideEffect.ShowFilterDialog -> {
                            showSnackbar(R.string.filter_dialog_not_implemented, Snackbar.LENGTH_SHORT)
                        }
                        is BrowseSideEffect.ShowError -> {
                            val errorMessage = effect.error.toErrorMessage(requireContext())
                            showSnackbar(
                                errorMessage, 
                                Snackbar.LENGTH_LONG,
                                getString(R.string.retry)
                            ) {
                                viewModel.onEvent(BrowseEvent.OnRetry)
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
        } else {
            binding.progressBar.gone()
        }
    }

    private fun buildBrowseItems(state: BrowseState.Success) {
        val browseItems = mutableListOf<BrowseItem>()

        browseItems.add(
            BrowseItem.Header(
                searchQuery = state.searchQuery,
                hasActiveFilters = state.hasActiveFilters
            )
        )

        browseItems.add(
            BrowseItem.Categories(
                categories = state.categories,
                selectedCategoryId = state.selectedCategoryId
            )
        )

        if (state.events.isEmpty()) {
            browseItems.add(BrowseItem.EmptyState)
        } else {
            state.events.forEach { event ->
                browseItems.add(BrowseItem.EventCard(event))
            }
        }

        browseAdapter.submitList(browseItems)
    }
}
