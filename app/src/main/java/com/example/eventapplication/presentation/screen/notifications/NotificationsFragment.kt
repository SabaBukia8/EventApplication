package com.example.eventapplication.presentation.screen.notifications

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eventapplication.MainActivity
import com.example.eventapplication.databinding.FragmentNotificationsBinding
import com.example.eventapplication.domain.model.Notification
import com.example.eventapplication.domain.model.NotificationTabCategory
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NotificationsFragment : Fragment(),
    NotificationDetailBottomSheet.NotificationActionListener {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NotificationsViewModel by viewModels()
    private val notificationsAdapter by lazy { NotificationsAdapter(::onNotificationClick) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        observeState()
        observeSideEffects()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onEvent(NotificationsEvent.RefreshNotifications)
    }

    private fun setupUI() {
        binding.apply {
            rvNotifications.apply {
                adapter = notificationsAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }

            swipeRefresh.setOnRefreshListener {
                viewModel.onEvent(NotificationsEvent.RefreshNotifications)
            }

            tabLayout.apply {
                addTab(newTab().setText("All"))
                addTab(newTab().setText("Registrations"))
                addTab(newTab().setText("Updates"))
                addTab(newTab().setText("Alerts"))

                addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                    override fun onTabSelected(tab: TabLayout.Tab) {
                        val category = when (tab.position) {
                            0 -> NotificationTabCategory.ALL
                            1 -> NotificationTabCategory.REGISTRATIONS
                            2 -> NotificationTabCategory.UPDATES
                            3 -> NotificationTabCategory.ALERTS
                            else -> NotificationTabCategory.ALL
                        }
                        viewModel.onEvent(NotificationsEvent.TabSelected(category))
                    }

                    override fun onTabUnselected(tab: TabLayout.Tab) {}
                    override fun onTabReselected(tab: TabLayout.Tab) {}
                })
            }

            toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }

            tvMarkAllRead.setOnClickListener {
                viewModel.onEvent(NotificationsEvent.MarkAllAsRead)
            }
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
                handleState(state)
            }
        }
    }

    private fun handleState(state: NotificationsState) {
        Log.d("NotificationsFragment", ">>> handleState: ${state::class.simpleName}")
        when (state) {
            NotificationsState.Idle -> {
                Log.d("NotificationsFragment", "State: Idle")
            }

            is NotificationsState.IsLoading -> {
                Log.d("NotificationsFragment", "State: Loading = ${state.isLoading}")
                binding.swipeRefresh.isRefreshing = state.isLoading
            }

            is NotificationsState.Success -> {
                Log.d(
                    "NotificationsFragment",
                    "✓ Success with ${state.notifications.size} items, ${state.unreadCount} unread"
                )
                notificationsAdapter.submitList(state.notifications)
                updateUnreadBadge(state.unreadCount)
                binding.tvMarkAllRead.isEnabled = state.unreadCount > 0
                binding.tvMarkAllRead.alpha = if (state.unreadCount > 0) 1.0f else 0.5f
            }

            is NotificationsState.Error -> {
                Log.e("NotificationsFragment", "✗ Error: ${state.error}")
                showError(state.error.toString())
            }
        }
    }

    private fun observeSideEffects() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.sideEffect.collect { sideEffect ->
                handleSideEffect(sideEffect)
            }
        }
    }

    private fun handleSideEffect(sideEffect: NotificationsSideEffect) {
        when (sideEffect) {
            is NotificationsSideEffect.ShowNotificationDetail -> {
                showNotificationDetailBottomSheet(sideEffect.notification)
            }

            is NotificationsSideEffect.ShowError -> {
                Toast.makeText(requireContext(), sideEffect.message, Toast.LENGTH_SHORT).show()
            }

            is NotificationsSideEffect.NavigateToEvent -> {
                // Navigate to event details if needed
            }
        }
    }

    private fun showNotificationDetailBottomSheet(notification: Notification) {
        NotificationDetailBottomSheet.newInstance(notification)
            .apply { setActionListener(this@NotificationsFragment) }
            .show(childFragmentManager, NotificationDetailBottomSheet.TAG)
    }

    private fun onNotificationClick(notification: Notification) {
        viewModel.onEvent(NotificationsEvent.NotificationClicked(notification))
    }

    private fun updateUnreadBadge(count: Int) {
        (requireActivity() as? MainActivity)?.updateNotificationBadge(count)
    }

    override fun onViewEventClicked(eventId: String?) {
        // Handle null eventId
        if (eventId == null) {
            Toast.makeText(requireContext(), "Event information not available", Toast.LENGTH_SHORT)
                .show()
            return
        }

        // Convert String to Int safely
        val eventIdInt = eventId.toIntOrNull()
        if (eventIdInt == null) {
            Toast.makeText(requireContext(), "Invalid event ID", Toast.LENGTH_SHORT).show()
            return
        }

        // Navigate to event details
        val action = NotificationsFragmentDirections
            .actionUpdatesFragmentToEventDetailsFragment(eventIdInt)
        findNavController().navigate(action)
    }

    override fun onMarkAsReadClicked(notificationId: String) {
        viewModel.onEvent(NotificationsEvent.MarkAsRead(notificationId))
    }

    private fun showError(message: String) {
        Log.e("NotificationsFragment", "Showing error to user: $message")

        // Toast
        Toast.makeText(requireContext(), "Error: $message", Toast.LENGTH_LONG).show()

        // Snackbar with retry
        Snackbar.make(binding.root, "Failed to load notifications", Snackbar.LENGTH_LONG)
            .setAction("Retry") {
                viewModel.onEvent(NotificationsEvent.RefreshNotifications)
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
