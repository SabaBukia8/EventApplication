package com.example.eventapplication.presentation.screen.eventdetails.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.eventapplication.R
import com.example.eventapplication.databinding.ItemEventDetailsActionBinding
import com.example.eventapplication.databinding.ItemEventDetailsAgendaBinding
import com.example.eventapplication.databinding.ItemEventDetailsDescriptionBinding
import com.example.eventapplication.databinding.ItemEventDetailsImageBinding
import com.example.eventapplication.databinding.ItemEventDetailsInfoBinding
import com.example.eventapplication.databinding.ItemEventDetailsSpeakersBinding
import com.example.eventapplication.presentation.extensions.toFormattedDateTime
import com.example.eventapplication.presentation.extensions.toTimeRange
import com.example.eventapplication.presentation.model.EventDetailsItem
import com.example.eventapplication.presentation.screen.home.mapper.EventTypeMapper

class EventDetailsAdapter(
    private val onBackClick: () -> Unit,
    private val onMenuClick: () -> Unit,
    private val onActionClick: () -> Unit
) : ListAdapter<EventDetailsItem, RecyclerView.ViewHolder>(EventDetailsItemDiffCallback()) {

    companion object {
        private const val VIEW_TYPE_IMAGE = 0
        private const val VIEW_TYPE_INFO = 1
        private const val VIEW_TYPE_ACTION = 2
        private const val VIEW_TYPE_DESCRIPTION = 3
        private const val VIEW_TYPE_AGENDA = 4
        private const val VIEW_TYPE_SPEAKERS = 5
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is EventDetailsItem.Image -> VIEW_TYPE_IMAGE
            is EventDetailsItem.Info -> VIEW_TYPE_INFO
            is EventDetailsItem.Action -> VIEW_TYPE_ACTION
            is EventDetailsItem.Description -> VIEW_TYPE_DESCRIPTION
            is EventDetailsItem.AgendaSection -> VIEW_TYPE_AGENDA
            is EventDetailsItem.SpeakersSection -> VIEW_TYPE_SPEAKERS
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_IMAGE -> {
                val binding = ItemEventDetailsImageBinding.inflate(inflater, parent, false)
                ImageViewHolder(binding, onBackClick, onMenuClick)
            }
            VIEW_TYPE_INFO -> {
                val binding = ItemEventDetailsInfoBinding.inflate(inflater, parent, false)
                InfoViewHolder(binding)
            }
            VIEW_TYPE_ACTION -> {
                val binding = ItemEventDetailsActionBinding.inflate(inflater, parent, false)
                ActionViewHolder(binding, onActionClick)
            }
            VIEW_TYPE_DESCRIPTION -> {
                val binding = ItemEventDetailsDescriptionBinding.inflate(inflater, parent, false)
                DescriptionViewHolder(binding)
            }
            VIEW_TYPE_AGENDA -> {
                val binding = ItemEventDetailsAgendaBinding.inflate(inflater, parent, false)
                AgendaViewHolder(binding)
            }
            VIEW_TYPE_SPEAKERS -> {
                val binding = ItemEventDetailsSpeakersBinding.inflate(inflater, parent, false)
                SpeakersViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is EventDetailsItem.Image -> (holder as ImageViewHolder).bind(item)
            is EventDetailsItem.Info -> (holder as InfoViewHolder).bind(item)
            is EventDetailsItem.Action -> (holder as ActionViewHolder).bind(item)
            is EventDetailsItem.Description -> (holder as DescriptionViewHolder).bind(item)
            is EventDetailsItem.AgendaSection -> (holder as AgendaViewHolder).bind(item)
            is EventDetailsItem.SpeakersSection -> (holder as SpeakersViewHolder).bind(item)
        }
    }

    class ImageViewHolder(
        private val binding: ItemEventDetailsImageBinding,
        private val onBackClick: () -> Unit,
        private val onMenuClick: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.ivBack.setOnClickListener { onBackClick() }
            binding.ivMenu.setOnClickListener { onMenuClick() }
        }

        fun bind(item: EventDetailsItem.Image) {
            with(binding) {
                val colorRes = with(EventTypeMapper) { item.eventType.toPlaceholderColor() }
                val color = root.context.getColor(colorRes)
                vImagePlaceholder.setBackgroundColor(color)
                // TODO: Load image if imageUrl is not null using Coil/Glide
            }
        }
    }

    class InfoViewHolder(
        private val binding: ItemEventDetailsInfoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: EventDetailsItem.Info) {
            with(binding) {
                val event = item.eventDetails

                tvCategory.text = with(EventTypeMapper) { event.eventType.toDisplayName(root.context) }

                tvTitle.text = event.title

                tvDate.text = event.startDateTime.toFormattedDateTime()
                tvTime.text = (event.startDateTime to event.endDateTime).toTimeRange()

                tvLocation.text = event.location

                val capacityText = root.context.getString(
                    R.string.event_capacity_info,
                    event.confirmedCount,
                    event.capacity,
                    event.spotsLeft
                )
                tvCapacity.text = capacityText

                if (event.tags.isNotEmpty()) {
                    tvTags.isVisible = true
                    tvTags.text = event.tags.joinToString(" ") { "#$it" }
                } else {
                    tvTags.isVisible = false
                }
            }
        }
    }

    class ActionViewHolder(
        private val binding: ItemEventDetailsActionBinding,
        private val onActionClick: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.btnAction.setOnClickListener { onActionClick() }
        }

        fun bind(item: EventDetailsItem.Action) {
            with(binding) {
                btnAction.text = item.buttonText
                btnAction.isEnabled = item.isEnabled && !item.isRegistering


                if (item.registrationDeadline != null) {
                    tvRegistrationDeadline.isVisible = true
                    tvRegistrationDeadline.text = item.registrationDeadline
                } else {
                    tvRegistrationDeadline.isVisible = false
                }
            }
        }
    }

    class DescriptionViewHolder(
        private val binding: ItemEventDetailsDescriptionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: EventDetailsItem.Description) {
            binding.tvDescription.text = item.description
        }
    }

    class AgendaViewHolder(
        binding: ItemEventDetailsAgendaBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val agendaAdapter = AgendaAdapter()

        init {
            binding.rvAgenda.apply {
                adapter = agendaAdapter
                layoutManager = LinearLayoutManager(context)
                setRecycledViewPool(recycledViewPool)
                isNestedScrollingEnabled = false
                setHasFixedSize(true)
            }
        }

        fun bind(item: EventDetailsItem.AgendaSection) {
            agendaAdapter.submitList(item.agendaItems)
        }
    }

    class SpeakersViewHolder(

        binding: ItemEventDetailsSpeakersBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val speakersAdapter = SpeakersAdapter()

        init {
            binding.rvSpeakers.apply {
                adapter = speakersAdapter
                layoutManager = LinearLayoutManager(context)
                setRecycledViewPool(recycledViewPool)
                isNestedScrollingEnabled = false
                setHasFixedSize(true)
            }
        }

        fun bind(item: EventDetailsItem.SpeakersSection) {
            speakersAdapter.submitList(item.speakers)
        }
    }

    private class EventDetailsItemDiffCallback : DiffUtil.ItemCallback<EventDetailsItem>() {
        override fun areItemsTheSame(oldItem: EventDetailsItem, newItem: EventDetailsItem): Boolean {
            return oldItem::class == newItem::class
        }

        override fun areContentsTheSame(oldItem: EventDetailsItem, newItem: EventDetailsItem): Boolean {
            return oldItem == newItem
        }
    }
}
