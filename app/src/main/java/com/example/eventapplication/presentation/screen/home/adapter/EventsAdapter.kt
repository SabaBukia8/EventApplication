package com.example.eventapplication.presentation.screen.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.eventapplication.R
import com.example.eventapplication.databinding.ItemEventCardBinding
import com.example.eventapplication.domain.model.Event
import com.example.eventapplication.presentation.extensions.toMonthAbbreviation
import com.example.eventapplication.presentation.extensions.toDayOfMonth
import com.example.eventapplication.presentation.extensions.toTimeRange
import com.example.eventapplication.presentation.extensions.visible
import com.example.eventapplication.presentation.extensions.gone

class EventsAdapter(
    private val onEventClick: (Int) -> Unit
) : ListAdapter<Event, EventsAdapter.EventViewHolder>(EventDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemEventCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EventViewHolder(binding, onEventClick)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class EventViewHolder(
        private val binding: ItemEventCardBinding,
        private val onEventClick: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(event: Event) {
            with(binding) {
                tvMonth.text = event.startDateTime.toMonthAbbreviation()
                tvDay.text = event.startDateTime.toDayOfMonth()

                tvTitle.text = event.title
                tvDescription.text = event.description
                tvLocation.text = event.location

                val timeRange = (event.startDateTime to event.endDateTime).toTimeRange()
                tvTime.text = timeRange

                if (event.isWaitlisted) {
                    tvWaitlistedBadge.visible()
                } else {
                    tvWaitlistedBadge.gone()
                }

                val registrationInfo = buildString {
                    append(root.context.getString(R.string.event_registered, event.confirmedCount))
                    if (!event.isFull) {
                        append(" • ")
                        append(root.context.getString(R.string.event_spots_left, event.spotsLeft))
                    } else {
                        append(" • ")
                        append(root.context.getString(R.string.event_full))
                    }
                }
                tvRegistrationInfo.text = registrationInfo

                root.setOnClickListener {
                    onEventClick(event.id)
                }

                tvViewDetails.setOnClickListener {
                    onEventClick(event.id)
                }
            }
        }
    }

    private class EventDiffCallback : DiffUtil.ItemCallback<Event>() {
        override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem == newItem
        }
    }
}
