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
    private var lastSuccessState: EventDetailsState.Success? = null

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
                android.util.Log.d("EventDetailsFragment", "Action button clicked!")
                val state = viewModel.state.value
                android.util.Log.d("EventDetailsFragment", "Current state: $state")

                if (state is EventDetailsState.Success) {
                    val status = state.eventDetails.registrationStatus
                    android.util.Log.d("EventDetailsFragment", "Registration status: $status")

                    when {
                        status == RegistrationStatus.CONFIRMED || status == RegistrationStatus.WAITLISTED -> {
                            android.util.Log.d("EventDetailsFragment", "Sending CancelRegistrationClicked event")
                            viewModel.onEvent(EventDetailsEvent.CancelRegistrationClicked)
                        }
                        status == RegistrationStatus.CANCELLED -> {
                            // Show toast explaining they can't re-register
                            android.util.Log.d("EventDetailsFragment", "Cannot re-register after cancellation")
                            android.widget.Toast.makeText(requireContext(), "You cannot re-register after cancelling", android.widget.Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            android.util.Log.d("EventDetailsFragment", "Sending RegisterClicked event")
                            viewModel.onEvent(EventDetailsEvent.RegisterClicked)
                        }
                    }
                } else {
                    android.util.Log.e("EventDetailsFragment", "State is not Success! State: $state")
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
        // If we have previous data, show it with button disabled during loading
        lastSuccessState?.let { state ->
            if (isLoading) {
                buildEventDetailsItems(state, canPerformAction = false)
            }
        }
    }

    private fun buildEventDetailsItems(state: EventDetailsState.Success, canPerformAction: Boolean = true) {
        lastSuccessState = state

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
            registrationDeadline = registrationDeadline,
            canPerformAction = canPerformAction
        ))

        items.add(EventDetailsItem.Description(event.description))

        // Agenda and Speakers sections skipped - backend doesn't provide this data yet

        detailsAdapter.submitList(items)
    }
}
