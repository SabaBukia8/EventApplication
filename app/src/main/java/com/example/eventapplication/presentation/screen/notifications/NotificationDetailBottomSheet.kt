package com.example.eventapplication.presentation.screen.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.eventapplication.R
import com.example.eventapplication.databinding.BottomSheetNotificationDetailBinding
import com.example.eventapplication.domain.model.Notification
import com.example.eventapplication.presentation.util.toFormattedDateTime
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class NotificationDetailBottomSheet : BottomSheetDialogFragment() {

    interface NotificationActionListener {
        fun onViewEventClicked(eventId: String?)
        fun onMarkAsReadClicked(notificationId: String)
    }

    override fun getTheme(): Int = R.style.Theme_App_BottomSheetDialog

    private var _binding: BottomSheetNotificationDetailBinding? = null
    private val binding get() = _binding!!

    private var actionListener: NotificationActionListener? = null

    private val notification: Notification by lazy {
        arguments?.getParcelable<Notification>(ARG_NOTIFICATION)
            ?: throw IllegalArgumentException("Notification required")
    }

    fun setActionListener(listener: NotificationActionListener) {
        this.actionListener = listener
    }

    companion object {
        const val TAG = "NotificationDetailBottomSheet"
        private const val ARG_NOTIFICATION = "notification"

        fun newInstance(notification: Notification): NotificationDetailBottomSheet {
            return NotificationDetailBottomSheet().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_NOTIFICATION, notification)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetNotificationDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        binding.apply {
            tvTitle.text = notification.title

            tvDateTime.text = notification.createdAt.toFormattedDateTime()

            // Hide location views (can be shown if location data is available)
            ivLocation.visibility = View.GONE
            tvLocation.visibility = View.GONE

            btnViewEvent.visibility = if (notification.eventId != null) View.VISIBLE else View.GONE

            btnClose.setOnClickListener {
                dismiss()
            }

            btnViewEvent.setOnClickListener {
                actionListener?.onViewEventClicked(notification.eventId)
                dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
