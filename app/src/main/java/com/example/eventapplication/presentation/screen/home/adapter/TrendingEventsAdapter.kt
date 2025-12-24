package com.example.eventapplication.presentation.screen.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.eventapplication.databinding.ItemTrendingEventBinding
import com.example.eventapplication.domain.model.Event
import com.example.eventapplication.presentation.extensions.toFormattedDateRange
import com.example.eventapplication.presentation.screen.home.mapper.EventTypeMapper.toPlaceholderColor

class TrendingEventsAdapter(
    private val onEventClick: (Int) -> Unit
) : ListAdapter<Event, TrendingEventsAdapter.TrendingEventViewHolder>(TrendingEventDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrendingEventViewHolder {
        val binding = ItemTrendingEventBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TrendingEventViewHolder(binding, onEventClick)
    }

    override fun onBindViewHolder(holder: TrendingEventViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class TrendingEventViewHolder(
        private val binding: ItemTrendingEventBinding,
        private val onEventClick: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(event: Event) {
            with(binding) {
                val colorRes = event.eventType.toPlaceholderColor()
                vImagePlaceholder.setBackgroundColor(root.context.getColor(colorRes))

                tvTitle.text = event.title

                tvDate.text = event.startDateTime.toFormattedDateRange(event.endDateTime)

                root.setOnClickListener {
                    onEventClick(event.id)
                }
            }
        }
    }

    private class TrendingEventDiffCallback : DiffUtil.ItemCallback<Event>() {
        override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem == newItem
        }
    }
}
