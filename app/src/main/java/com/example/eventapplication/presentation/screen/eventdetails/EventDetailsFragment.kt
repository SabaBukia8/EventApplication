package com.example.eventapplication.presentation.screen.eventdetails

import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.eventapplication.R
import com.example.eventapplication.databinding.FragmentEventDetailsBinding
import com.example.eventapplication.domain.model.RegistrationStatus
import com.example.eventapplication.presentation.common.BaseFragment
import com.example.eventapplication.presentation.screen.eventdetails.EventDetailsFragmentArgs
import com.example.eventapplication.presentation.extensions.showSnackbar
import com.example.eventapplication.presentation.extensions.toErrorMessage
import com.example.eventapplication.presentation.extensions.toRegistrationDeadlineText
import com.example.eventapplication.presentation.screen.eventdetails.adapter.EventDetailsAdapter
import com.example.eventapplication.presentation.model.EventDetailsItem
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EventDetailsFragment : BaseFragment<FragmentEventDetailsBinding>(
    FragmentEventDetailsBinding::inflate
) {

    private val viewModel: EventDetailsViewModel by viewModels()
    private lateinit var detailsAdapter: EventDetailsAdapter
    private val args: EventDetailsFragmentArgs by navArgs()

    override fun listeners() {
        setupAdapter()
        observeState()
        observeSideEffects()

        viewModel.onEvent(EventDetailsEvent.LoadEventDetails(args.eventId))
    }

    private fun setupAdapter() {
        detailsAdapter = EventDetailsAdapter(
            onBackClick = {
                viewModel.onEvent(EventDetailsEvent.BackClicked)
            },
            onMenuClick = {
                showSnackbar(R.string.menu, Snackbar.LENGTH_SHORT)
            },
            onActionClick = {
                val state = viewModel.state.value
                if (state is EventDetailsState.Success) {
                    val registrationStatus = state.eventDetails.registrationStatus
                    if (registrationStatus != null &&
                        (registrationStatus == RegistrationStatus.CONFIRMED ||
                         registrationStatus == RegistrationStatus.WAITLISTED)) {
                        viewModel.onEvent(EventDetailsEvent.CancelRegistrationClicked)
                    } else {
                        viewModel.onEvent(EventDetailsEvent.RegisterClicked)
                    }
                }
            }
        )

        binding.rvEventDetails.adapter = detailsAdapter
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    when (state) {
                        is EventDetailsState.IsLoading -> handleLoading(state.isLoading)
                        is EventDetailsState.Success -> buildEventDetailsItems(state)
                        is EventDetailsState.Error -> {}
                        is EventDetailsState.Idle -> {}
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
                        is EventDetailsSideEffect.NavigateBack -> {
                            findNavController().navigateUp()
                        }
                        is EventDetailsSideEffect.ShowError -> {
                            val errorMessage = effect.error.toErrorMessage(requireContext())
                            showSnackbar(errorMessage, Snackbar.LENGTH_LONG)
                        }
                        is EventDetailsSideEffect.ShowToast -> {
                            showSnackbar(effect.message, Snackbar.LENGTH_SHORT)
                        }
                        is EventDetailsSideEffect.ShowToastResource -> {
                            showSnackbar(getString(effect.messageResId), Snackbar.LENGTH_SHORT)
                        }
                    }
                }
            }
        }
    }

    private fun handleLoading(isLoading: Boolean) {
    }

    private fun buildEventDetailsItems(state: EventDetailsState.Success) {
        val event = state.eventDetails
        val items = mutableListOf<EventDetailsItem>()

        items.add(EventDetailsItem.Image(
            imageUrl = event.imageUrl,
            eventType = event.eventType
        ))

        items.add(EventDetailsItem.Info(event))

        val buttonText = viewModel.getButtonText(event.registrationStatus, event.isFull)
        val isEnabled = viewModel.isButtonEnabled(event.registrationStatus)
        val registrationDeadline = event.registrationDeadline?.toRegistrationDeadlineText()
        items.add(EventDetailsItem.Action(
            buttonText = buttonText,
            isEnabled = isEnabled,
            capacityText = event.capacityText,
            isRegistering = state.isRegistering,
            registrationDeadline = registrationDeadline
        ))

        items.add(EventDetailsItem.Description(event.description))

        if (event.agenda.isNotEmpty()) {
            items.add(EventDetailsItem.AgendaSection(event.agenda))
        }

        if (event.speakers.isNotEmpty()) {
            items.add(EventDetailsItem.SpeakersSection(event.speakers))
        }

        detailsAdapter.submitList(items)
    }
}
