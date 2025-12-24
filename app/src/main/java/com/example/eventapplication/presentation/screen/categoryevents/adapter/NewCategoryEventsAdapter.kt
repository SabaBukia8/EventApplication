package com.example.eventapplication.presentation.screen.categoryevents.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.eventapplication.R
import com.example.eventapplication.databinding.ItemCategoryEventCardNewBinding
import com.example.eventapplication.databinding.ItemCategoryEventsHeaderBinding
import com.example.eventapplication.domain.model.EventType
import com.example.eventapplication.presentation.model.CategoryEventsItem
import com.example.eventapplication.presentation.model.FilterType
import com.example.eventapplication.presentation.extensions.gone
import com.example.eventapplication.presentation.extensions.toFormattedDate
import com.example.eventapplication.presentation.extensions.toTimeString
import com.example.eventapplication.presentation.extensions.visible


class NewCategoryEventsAdapter(
    private val onBackClick: () -> Unit,
    private val onNotificationClick: () -> Unit,
    private val onFilterClick: (FilterType) -> Unit,
    private val onEventClick: (Int) -> Unit
) : ListAdapter<CategoryEventsItem, RecyclerView.ViewHolder>(CategoryEventsItemDiffCallback()) {

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_EVENT_CARD = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is CategoryEventsItem.Header -> VIEW_TYPE_HEADER
            is CategoryEventsItem.EventCard -> VIEW_TYPE_EVENT_CARD
            is CategoryEventsItem.FilterChips -> VIEW_TYPE_HEADER 
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_HEADER -> {
                val binding = ItemCategoryEventsHeaderBinding.inflate(inflater, parent, false)
                HeaderViewHolder(binding, onBackClick, onNotificationClick, onFilterClick)
            }
            VIEW_TYPE_EVENT_CARD -> {
                val binding = ItemCategoryEventCardNewBinding.inflate(inflater, parent, false)
                EventCardViewHolder(binding, onEventClick)
            }
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is CategoryEventsItem.Header -> (holder as HeaderViewHolder).bind(item)
            is CategoryEventsItem.EventCard -> (holder as EventCardViewHolder).bind(item)
            is CategoryEventsItem.FilterChips -> {} // Handled in header
        }
    }


    class HeaderViewHolder(
        private val binding: ItemCategoryEventsHeaderBinding,
        private val onBackClick: () -> Unit,
        private val onNotificationClick: () -> Unit,
        private val onFilterClick: (FilterType) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CategoryEventsItem.Header) {
            with(binding) {
                tvCategoryTitle.text = item.categoryName
                
                if (item.hasNotifications) {
                    vNotificationBadge.visible()
                } else {
                    vNotificationBadge.gone()
                }
                
                btnBack.setOnClickListener { onBackClick() }
                flNotificationButton.setOnClickListener { onNotificationClick() }
                
                updateFilterChips(item.selectedFilter)
                
                chipAllEvents.setOnClickListener { onFilterClick(FilterType.ALL_EVENTS) }
                chipDate.setOnClickListener { onFilterClick(FilterType.DATE) }
                chipLocation.setOnClickListener { onFilterClick(FilterType.LOCATION) }
            }
        }

        private fun updateFilterChips(selectedFilter: FilterType) {
            with(binding) {
                chipAllEvents.apply {
                    setBackgroundColor(context.getColor(
                        if (selectedFilter == FilterType.ALL_EVENTS) R.color.black else R.color.otp_box_background
                    ))
                    setTextColor(context.getColor(
                        if (selectedFilter == FilterType.ALL_EVENTS) R.color.white else R.color.label_text
                    ))
                    iconTint = context.getColorStateList(
                        if (selectedFilter == FilterType.ALL_EVENTS) R.color.white else R.color.label_text
                    )
                }

                chipDate.apply {
                    setBackgroundColor(context.getColor(
                        if (selectedFilter == FilterType.DATE) R.color.black else R.color.otp_box_background
                    ))
                    setTextColor(context.getColor(
                        if (selectedFilter == FilterType.DATE) R.color.white else R.color.label_text
                    ))
                }

                chipLocation.apply {
                    setBackgroundColor(context.getColor(
                        if (selectedFilter == FilterType.LOCATION) R.color.black else R.color.otp_box_background
                    ))
                    setTextColor(context.getColor(
                        if (selectedFilter == FilterType.LOCATION) R.color.white else R.color.label_text
                    ))
                }
            }
        }
    }
    
    class EventCardViewHolder(
        private val binding: ItemCategoryEventCardNewBinding,
        private val onEventClick: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CategoryEventsItem.EventCard) {
            val event = item.event
            with(binding) {
                tvImagePlaceholder.text = root.context.getString(R.string.image_placeholder)
                
                tvEventTitle.text = event.title
                
                tvCategoryBadge.text = formatEventType(event.eventType)
                
                tvEventDescription.text = event.description
                
                tvEventDate.text = event.startDateTime.toFormattedDate()
                
                tvEventTime.text = event.startDateTime.toTimeString(event.endDateTime)
                
                tvEventLocation.text = event.location
                
                if (item.isRegistered) {
                    btnAction.text = root.context.getString(R.string.registered)
                    btnAction.setBackgroundColor(root.context.getColor(R.color.white))
                    btnAction.setTextColor(root.context.getColor(R.color.text_primary))
                    btnAction.strokeColor = root.context.getColorStateList(R.color.border_gray)
                    btnAction.strokeWidth = 2
                } else if (item.isWaitlisted) {
                    btnAction.text = root.context.getString(R.string.event_waitlisted)
                    btnAction.setBackgroundColor(root.context.getColor(R.color.white))
                    btnAction.setTextColor(root.context.getColor(R.color.text_primary))
                    btnAction.strokeColor = root.context.getColorStateList(R.color.border_gray)
                    btnAction.strokeWidth = 2
                } else {
                    btnAction.text = root.context.getString(R.string.view_details)
                    btnAction.setBackgroundColor(root.context.getColor(R.color.black))
                    btnAction.setTextColor(root.context.getColor(R.color.white))
                    btnAction.strokeWidth = 0
                }
                
                root.setOnClickListener { onEventClick(event.id) }
                btnAction.setOnClickListener { onEventClick(event.id) }
            }
        }

        private fun formatEventType(eventType: EventType): String {
            val context = binding.root.context
            return when (eventType) {
                EventType.TEAM_BUILDING -> context.getString(R.string.category_type_team_building)
                EventType.SPORTS -> context.getString(R.string.category_type_sports)
                EventType.WORKSHOP -> context.getString(R.string.category_type_workshop)
                EventType.HAPPY_FRIDAY -> context.getString(R.string.category_type_happy_friday)
                EventType.CULTURAL -> context.getString(R.string.category_type_cultural)
                EventType.WELLNESS -> context.getString(R.string.category_type_wellness)
                EventType.TRAINING -> context.getString(R.string.training)
                    EventType.SOCIAL -> context.getString(R.string.social)
                        EventType.CONFERENCE -> context.getString(R.string.conference)
                    EventType.OTHER -> context.getString(R.string.other)
            }
        }
    }
    
    private class CategoryEventsItemDiffCallback : DiffUtil.ItemCallback<CategoryEventsItem>() {
        override fun areItemsTheSame(
            oldItem: CategoryEventsItem,
            newItem: CategoryEventsItem
        ): Boolean {
            return when {
                oldItem is CategoryEventsItem.Header && newItem is CategoryEventsItem.Header -> true
                oldItem is CategoryEventsItem.EventCard && newItem is CategoryEventsItem.EventCard ->
                    oldItem.event.id == newItem.event.id
                else -> false
            }
        }

        override fun areContentsTheSame(
            oldItem: CategoryEventsItem,
            newItem: CategoryEventsItem
        ): Boolean {
            return oldItem == newItem
        }
    }
}
