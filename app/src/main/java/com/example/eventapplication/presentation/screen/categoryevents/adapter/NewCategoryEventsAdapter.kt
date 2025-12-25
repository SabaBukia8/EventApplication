package com.example.eventapplication.presentation.screen.categoryevents.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.eventapplication.R
import com.example.eventapplication.databinding.ItemCategoryEventCardNewBinding
import com.example.eventapplication.databinding.ItemCategoryEventsHeaderBinding
import com.example.eventapplication.domain.model.Event
import com.example.eventapplication.domain.model.EventRegistrationStatus
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
    private val onLocationSelected: (String?) -> Unit,
    private val onDateRangeClick: () -> Unit,
    private val onAvailabilityToggled: (Boolean) -> Unit,
    private val onClearFiltersClick: () -> Unit,
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
                HeaderViewHolder(binding, onBackClick, onNotificationClick, onLocationSelected,
                    onDateRangeClick, onAvailabilityToggled, onClearFiltersClick)
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
        private val onLocationSelected: (String?) -> Unit,
        private val onDateRangeClick: () -> Unit,
        private val onAvailabilityToggled: (Boolean) -> Unit,
        private val onClearFiltersClick: () -> Unit
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

                setupLocationSpinner(item.availableLocations, item.selectedLocation)

                btnDateRange.text = item.dateRangeText ?: root.context.getString(R.string.select_date_range)
                btnDateRange.setOnClickListener { onDateRangeClick() }

                switchOnlyAvailable.setOnCheckedChangeListener(null)
                switchOnlyAvailable.isChecked = item.onlyAvailable
                switchOnlyAvailable.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked != item.onlyAvailable) {
                        onAvailabilityToggled(isChecked)
                    }
                }

                if (item.hasActiveFilters) {
                    btnClearFilters.visible()
                    btnClearFilters.setOnClickListener { onClearFiltersClick() }
                } else {
                    btnClearFilters.gone()
                }
            }
        }

        private fun setupLocationSpinner(locations: List<String>, selectedLocation: String?) {
            val context = binding.root.context
            val items = listOf(context.getString(R.string.all_locations)) + locations
            val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, items)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            binding.spinnerLocation.adapter = adapter

            val selectedIndex = if (selectedLocation != null) {
                locations.indexOf(selectedLocation) + 1
            } else 0

            binding.spinnerLocation.onItemSelectedListener = null
            binding.spinnerLocation.setSelection(selectedIndex)

            binding.spinnerLocation.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val location = if (position == 0) null else locations[position - 1]
                    if (location != selectedLocation) {
                        onLocationSelected(location)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
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

                displayRegistrationStatus(item.registrationStatus, event)

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

        private fun displayRegistrationStatus(
            status: EventRegistrationStatus?,
            event: Event
        ) = with(binding){
            val context = root.context

            val badgeText = when {
                status?.isRegistered == true -> context.getString(R.string.status_registered)
                status?.isWaitlisted == true -> context.getString(R.string.status_waitlisted)
                event.isFull -> context.getString(R.string.status_full)
                else -> {
                    val spotsLeft = event.spotsLeft.coerceAtLeast(0)
                    context.getString(R.string.spots_left_format, spotsLeft)
                }
            }

            tvCategoryBadge.text = badgeText
            tvCategoryBadge.backgroundTintList =
                context.getColorStateList(R.color.badge_waitlist)
            tvCategoryBadge.setTextColor(context.getColor(R.color.badge_waitlist_text))
            tvCategoryBadge.visible()
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
