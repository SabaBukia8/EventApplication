package com.example.eventapplication.presentation.screen.myevents

import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eventapplication.databinding.FragmentMyEventsBinding
import com.example.eventapplication.presentation.common.BaseFragment
import com.example.eventapplication.presentation.extensions.gone
import com.example.eventapplication.presentation.extensions.showSnackbar
import com.example.eventapplication.presentation.extensions.visible
import com.example.eventapplication.presentation.model.MyEventsItem
import com.example.eventapplication.presentation.screen.myevents.adapter.MyEventsAdapter
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyEventsFragment : BaseFragment<FragmentMyEventsBinding>(
    FragmentMyEventsBinding::inflate
) {

    private val viewModel: MyEventsViewModel by viewModels()
    private lateinit var myEventsAdapter: MyEventsAdapter

    override fun listeners() {
        setupAdapter()
        setupSwipeRefresh()
        observeState()
        observeSideEffects()

        viewModel.onEvent(MyEventsEvent.LoadRegistrations)
    }

    private fun setupAdapter() {
        myEventsAdapter = MyEventsAdapter(
            onEventClick = { eventId ->
                viewModel.onEvent(MyEventsEvent.OnEventClicked(eventId))
            },
            onCalendarClick = {
                viewModel.onEvent(MyEventsEvent.OnCalendarViewClicked)
            },
            onBrowseEventsClick = {
                navigateToBrowse()
            }
        )

        binding.rvMyEvents.apply {
            adapter = myEventsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.onEvent(MyEventsEvent.OnRefresh)
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    when (state) {
                        is MyEventsState.Idle -> {}
                        is MyEventsState.IsLoading -> handleLoading(state.isLoading)
                        is MyEventsState.Success -> handleSuccess(state)
                        is MyEventsState.Empty -> handleEmpty()
                        is MyEventsState.Error -> handleError(state.message)
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
                        is MyEventsSideEffect.NavigateToEventDetails -> {
                            val action = MyEventsFragmentDirections
                                .actionMyEventsFragmentToEventDetailsFragment(effect.eventId)
                            findNavController().navigate(action)
                        }
                        is MyEventsSideEffect.ShowMessage -> {
                            showSnackbar(effect.message, Snackbar.LENGTH_SHORT)
                        }
                    }
                }
            }
        }
    }

    private fun handleLoading(isLoading: Boolean) {
        with(binding) {
            if (isLoading) {
                if (!swipeRefresh.isRefreshing) {
                    progressBar.visible()
                }
                llErrorState.gone()
            } else {
                progressBar.gone()
                swipeRefresh.isRefreshing = false
            }
        }
    }

    private fun handleSuccess(state: MyEventsState.Success) {
        with(binding) {
            progressBar.gone()
            swipeRefresh.isRefreshing = false
            llErrorState.gone()
            rvMyEvents.visible()

            val items = buildMyEventsItems(state)
            myEventsAdapter.submitList(items)
        }
    }

    private fun handleEmpty() {
        with(binding) {
            progressBar.gone()
            swipeRefresh.isRefreshing = false
            llErrorState.gone()
            rvMyEvents.visible()

            val items = listOf(
                MyEventsItem.Header(),
                MyEventsItem.Empty
            )
            myEventsAdapter.submitList(items)
        }
    }

    private fun handleError(message: String) {
        with(binding) {
            progressBar.gone()
            swipeRefresh.isRefreshing = false
            rvMyEvents.gone()
            llErrorState.visible()

            tvErrorMessage.text = message
            btnRetry.setOnClickListener {
                viewModel.onEvent(MyEventsEvent.OnRetry)
            }
        }
    }

    private fun buildMyEventsItems(state: MyEventsState.Success): List<MyEventsItem> {
        val items = mutableListOf<MyEventsItem>()

        // Add header
        items.add(MyEventsItem.Header())

        // Add event cards
        state.registrations.forEach { registration ->
            items.add(
                MyEventsItem.EventCard(
                    registration = registration,
                    isNextUpcoming = registration.eventId == state.nextUpcomingEventId
                )
            )
        }

        return items
    }

    private fun navigateToBrowse() {
        try {
            findNavController().navigate(
                MyEventsFragmentDirections.actionMyEventsFragmentToBrowseFragment()
            )
        } catch (e: Exception) {
            showSnackbar("Unable to navigate to browse", Snackbar.LENGTH_SHORT)
        }
    }
}
