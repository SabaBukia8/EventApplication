package com.example.eventapplication.presentation.screen.myevents.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.eventapplication.R
import com.example.eventapplication.databinding.ItemMyEventCardBinding
import com.example.eventapplication.databinding.ItemMyEventsEmptyBinding
import com.example.eventapplication.databinding.ItemMyEventsHeaderBinding
import com.example.eventapplication.domain.model.RegistrationStatus
import com.example.eventapplication.presentation.extensions.*
import com.example.eventapplication.presentation.model.MyEventsItem

class MyEventsAdapter(
    private val onEventClick: (Int) -> Unit,
    private val onCalendarClick: () -> Unit,
    private val onBrowseEventsClick: () -> Unit
) : ListAdapter<MyEventsItem, RecyclerView.ViewHolder>(MyEventsItemDiffCallback()) {

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_EVENT_CARD = 1
        private const val VIEW_TYPE_EMPTY = 2
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is MyEventsItem.Header -> VIEW_TYPE_HEADER
            is MyEventsItem.EventCard -> VIEW_TYPE_EVENT_CARD
            is MyEventsItem.Empty -> VIEW_TYPE_EMPTY
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_HEADER -> {
                val binding = ItemMyEventsHeaderBinding.inflate(inflater, parent, false)
                HeaderViewHolder(binding, onCalendarClick)
            }
            VIEW_TYPE_EVENT_CARD -> {
                val binding = ItemMyEventCardBinding.inflate(inflater, parent, false)
                EventCardViewHolder(binding, onEventClick)
            }
            VIEW_TYPE_EMPTY -> {
                val binding = ItemMyEventsEmptyBinding.inflate(inflater, parent, false)
                EmptyViewHolder(binding, onBrowseEventsClick)
            }
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is MyEventsItem.Header -> (holder as HeaderViewHolder).bind(item)
            is MyEventsItem.EventCard -> (holder as EventCardViewHolder).bind(item)
            is MyEventsItem.Empty -> {} // No binding needed for empty state
        }
    }

    class HeaderViewHolder(
        private val binding: ItemMyEventsHeaderBinding,
        private val onCalendarClick: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MyEventsItem.Header) {
            with(binding) {
                tvTitle.text = item.title
                btnCalendar.setOnClickListener {
                    onCalendarClick()
                }
            }
        }
    }

    class EventCardViewHolder(
        private val binding: ItemMyEventCardBinding,
        private val onEventClick: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MyEventsItem.EventCard) {
            val registration = item.registration
            val event = registration.event

            with(binding) {
                // Set left border color for highlighting
                if (item.isNextUpcoming) {
                    vLeftBorder.setBackgroundColor(
                        ContextCompat.getColor(root.context, R.color.text_primary)
                    )
                } else {
                    vLeftBorder.setBackgroundColor(Color.TRANSPARENT)
                }

                // Date badge
                tvMonth.text = event.startDateTime.toMonthAbbreviation()
                tvDay.text = event.startDateTime.toDayOfMonth()

                // Status badge
                when (registration.status) {
                    RegistrationStatus.CONFIRMED -> {
                        tvStatusBadge.visible()
                        tvStatusBadge.text = root.context.getString(R.string.badge_confirmed)
                        tvStatusBadge.setBackgroundColor(
                            ContextCompat.getColor(root.context, R.color.badge_registered)
                        )
                        tvStatusBadge.setTextColor(
                            ContextCompat.getColor(root.context, R.color.badge_registered_text)
                        )
                    }
                    RegistrationStatus.WAITLISTED -> {
                        tvStatusBadge.visible()
                        tvStatusBadge.text = root.context.getString(R.string.badge_waitlisted)
                        tvStatusBadge.setBackgroundColor(
                            ContextCompat.getColor(root.context, R.color.badge_waitlist)
                        )
                        tvStatusBadge.setTextColor(
                            ContextCompat.getColor(root.context, R.color.badge_waitlist_text)
                        )
                    }
                    else -> {
                        tvStatusBadge.gone()
                    }
                }

                // Event details
                tvTitle.text = event.title
                tvTime.text = event.startDateTime.toTimeString(event.endDateTime)
                tvLocation.text = event.location

                // Click handler
                root.setOnClickListener {
                    onEventClick(event.id)
                }
            }
        }
    }

    class EmptyViewHolder(
        private val binding: ItemMyEventsEmptyBinding,
        private val onBrowseEventsClick: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.btnBrowseEvents.setOnClickListener {
                onBrowseEventsClick()
            }
        }
    }

    class MyEventsItemDiffCallback : DiffUtil.ItemCallback<MyEventsItem>() {
        override fun areItemsTheSame(oldItem: MyEventsItem, newItem: MyEventsItem): Boolean {
            return when {
                oldItem is MyEventsItem.Header && newItem is MyEventsItem.Header -> true
                oldItem is MyEventsItem.Empty && newItem is MyEventsItem.Empty -> true
                oldItem is MyEventsItem.EventCard && newItem is MyEventsItem.EventCard ->
                    oldItem.registration.registrationId == newItem.registration.registrationId
                else -> false
            }
        }

        override fun areContentsTheSame(oldItem: MyEventsItem, newItem: MyEventsItem): Boolean {
            return oldItem == newItem
        }
    }
}
