package com.example.eventapplication.presentation.screen.browse.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.eventapplication.R
import com.example.eventapplication.databinding.ItemBrowseCategoriesBinding
import com.example.eventapplication.databinding.ItemBrowseEmptyStateBinding
import com.example.eventapplication.databinding.ItemBrowseEventCardBinding
import com.example.eventapplication.databinding.ItemBrowseHeaderBinding
import com.example.eventapplication.domain.model.Event
import com.example.eventapplication.presentation.model.BrowseItem
import com.example.eventapplication.presentation.extensions.gone
import com.example.eventapplication.presentation.extensions.toFormattedDate
import com.example.eventapplication.presentation.extensions.toTimeRange
import com.example.eventapplication.presentation.extensions.visible
import androidx.core.graphics.toColorInt

class BrowseAdapter(
    private val onSearchQueryChanged: (String) -> Unit,
    private val onFilterClick: () -> Unit,
    private val onCategoryClick: (Int?) -> Unit,
    private val onEventClick: (Int) -> Unit,
    private val onClearFilters: () -> Unit
) : ListAdapter<BrowseItem, RecyclerView.ViewHolder>(BrowseItemDiffCallback()) {

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_CATEGORIES = 1
        private const val VIEW_TYPE_EVENT = 2
        private const val VIEW_TYPE_EMPTY = 3
    }

    override fun getItemViewType(position: Int): Int =
        when (getItem(position)) {
            is BrowseItem.Header -> VIEW_TYPE_HEADER
            is BrowseItem.Categories -> VIEW_TYPE_CATEGORIES
            is BrowseItem.EventCard -> VIEW_TYPE_EVENT
            is BrowseItem.EmptyState -> VIEW_TYPE_EMPTY
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_HEADER -> {
                val binding = ItemBrowseHeaderBinding.inflate(inflater, parent, false)
                HeaderViewHolder(binding, onSearchQueryChanged, onFilterClick, onClearFilters)
            }
            VIEW_TYPE_CATEGORIES -> {
                val binding = ItemBrowseCategoriesBinding.inflate(inflater, parent, false)
                CategoriesViewHolder(binding, onCategoryClick)
            }
            VIEW_TYPE_EVENT -> {
                val binding = ItemBrowseEventCardBinding.inflate(inflater, parent, false)
                EventViewHolder(binding, onEventClick)
            }
            VIEW_TYPE_EMPTY -> {
                val binding = ItemBrowseEmptyStateBinding.inflate(inflater, parent, false)
                EmptyStateViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is BrowseItem.Header -> (holder as HeaderViewHolder).bind(item)
            is BrowseItem.Categories -> (holder as CategoriesViewHolder).bind(item)
            is BrowseItem.EventCard -> (holder as EventViewHolder).bind(item.event)
            is BrowseItem.EmptyState -> (holder as EmptyStateViewHolder).bind()
        }
    }

    class HeaderViewHolder(
        private val binding: ItemBrowseHeaderBinding,
        private val onSearchQueryChanged: (String) -> Unit,
        private val onFilterClick: () -> Unit,
        private val onClearFilters: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private var textWatcher: TextWatcher? = null

        init {
            binding.btnFilter.setOnClickListener { onFilterClick() }
            binding.btnClearFilters.setOnClickListener { onClearFilters() }
        }

        fun bind(item: BrowseItem.Header) = with(binding) {
            etSearch.removeTextChangedListener(textWatcher)

            if (etSearch.text.toString() != item.searchQuery) {
                etSearch.setText(item.searchQuery)
                etSearch.setSelection(item.searchQuery.length)
            }

            textWatcher = object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    onSearchQueryChanged(s?.toString() ?: "")
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            }
            etSearch.addTextChangedListener(textWatcher)

            if (item.hasActiveFilters) {
                btnClearFilters.visible()
            } else {
                btnClearFilters.gone()
            }
        }
    }

    class CategoriesViewHolder(
        binding: ItemBrowseCategoriesBinding,
        onCategoryClick: (Int?) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private val categoryChipsAdapter = CategoryChipsAdapter(onCategoryClick)

        init {
            binding.rvCategories.apply {
                adapter = categoryChipsAdapter
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            }
        }

        fun bind(item: BrowseItem.Categories) {
            categoryChipsAdapter.submitList(item.categories, item.selectedCategoryId)
        }
    }

    class EventViewHolder(
        private val binding: ItemBrowseEventCardBinding,
        private val onEventClick: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(event: Event) {
            with(binding) {
                val formattedDate = event.startDateTime.toFormattedDate()
                val parts = formattedDate.split(" ")
                if (parts.size == 2) {
                    tvMonth.text = parts[0]
                    tvDay.text = parts[1]
                }

                tvTitle.text = event.title
                tvLocation.text = event.location

                val timeRange = (event.startDateTime to event.endDateTime).toTimeRange()
                tvTime.text = timeRange

                when {
                    event.isFull -> {
                        tvCapacityBadge.visible()
                        tvCapacityBadge.text = root.context.getString(R.string.event_full)
                        tvCapacityBadge.setBackgroundColor("#EF4444".toColorInt())
                    }
                    event.spotsLeft <= 10 -> {
                        tvCapacityBadge.visible()
                        tvCapacityBadge.text = root.context.getString(R.string.event_spots_left, event.spotsLeft)
                        tvCapacityBadge.setBackgroundColor("#F59E0B".toColorInt())
                    }
                    else -> {
                        tvCapacityBadge.gone()
                    }
                }

                root.setOnClickListener { onEventClick(event.id) }
            }
        }
    }

    class EmptyStateViewHolder(
        binding: ItemBrowseEmptyStateBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
        }
    }

    private class BrowseItemDiffCallback : DiffUtil.ItemCallback<BrowseItem>() {
        override fun areItemsTheSame(oldItem: BrowseItem, newItem: BrowseItem): Boolean =
            when {
                oldItem is BrowseItem.Header && newItem is BrowseItem.Header -> true
                oldItem is BrowseItem.Categories && newItem is BrowseItem.Categories -> true
                oldItem is BrowseItem.EventCard && newItem is BrowseItem.EventCard ->
                    oldItem.event.id == newItem.event.id
                oldItem is BrowseItem.EmptyState && newItem is BrowseItem.EmptyState -> true
                else -> false
            }

        override fun areContentsTheSame(oldItem: BrowseItem, newItem: BrowseItem): Boolean =
            oldItem == newItem
    }
}
