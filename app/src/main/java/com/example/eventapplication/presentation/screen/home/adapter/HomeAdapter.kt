package com.example.eventapplication.presentation.screen.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.eventapplication.R
import com.example.eventapplication.databinding.ItemHomeCategoriesBinding
import com.example.eventapplication.databinding.ItemHomeHeaderBinding
import com.example.eventapplication.databinding.ItemHomeTrendingBinding
import com.example.eventapplication.databinding.ItemHomeUpcomingEventsBinding
import com.example.eventapplication.databinding.ItemHomeWelcomeBinding
import com.example.eventapplication.presentation.extensions.gone
import com.example.eventapplication.presentation.extensions.visible
import com.example.eventapplication.presentation.model.HomeItem

class HomeAdapter(
    private val onEventClick: (Int) -> Unit,
    private val onCategoryClick: (Int) -> Unit,
    private val onViewAllClick: () -> Unit,
    private val onNotificationClick: () -> Unit,
    private val onProfileClick: () -> Unit
) : ListAdapter<HomeItem, RecyclerView.ViewHolder>(HomeItemDiffCallback()) {

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_WELCOME = 1
        private const val VIEW_TYPE_UPCOMING_EVENTS = 2
        private const val VIEW_TYPE_CATEGORIES = 3
        private const val VIEW_TYPE_TRENDING = 4
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is HomeItem.Header -> VIEW_TYPE_HEADER
            is HomeItem.Welcome -> VIEW_TYPE_WELCOME
            is HomeItem.UpcomingEventsSection -> VIEW_TYPE_UPCOMING_EVENTS
            is HomeItem.CategoriesSection -> VIEW_TYPE_CATEGORIES
            is HomeItem.TrendingEventsSection -> VIEW_TYPE_TRENDING
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_HEADER -> {
                val binding = ItemHomeHeaderBinding.inflate(inflater, parent, false)
                HeaderViewHolder(binding, onNotificationClick, onProfileClick)
            }
            VIEW_TYPE_WELCOME -> {
                val binding = ItemHomeWelcomeBinding.inflate(inflater, parent, false)
                WelcomeViewHolder(binding)
            }
            VIEW_TYPE_UPCOMING_EVENTS -> {
                val binding = ItemHomeUpcomingEventsBinding.inflate(inflater, parent, false)
                UpcomingEventsViewHolder(binding, onEventClick, onViewAllClick)
            }
            VIEW_TYPE_CATEGORIES -> {
                val binding = ItemHomeCategoriesBinding.inflate(inflater, parent, false)
                CategoriesViewHolder(binding, onCategoryClick)
            }
            VIEW_TYPE_TRENDING -> {
                val binding = ItemHomeTrendingBinding.inflate(inflater, parent, false)
                TrendingViewHolder(binding, onEventClick)
            }
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is HomeItem.Header -> (holder as HeaderViewHolder).bind(item)
            is HomeItem.Welcome -> (holder as WelcomeViewHolder).bind(item)
            is HomeItem.UpcomingEventsSection -> (holder as UpcomingEventsViewHolder).bind(item)
            is HomeItem.CategoriesSection -> (holder as CategoriesViewHolder).bind(item)
            is HomeItem.TrendingEventsSection -> (holder as TrendingViewHolder).bind(item)
        }
    }

    class HeaderViewHolder(
        private val binding: ItemHomeHeaderBinding,
        private val onNotificationClick: () -> Unit,
        private val onProfileClick: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: HomeItem.Header) {
            with(binding) {
                if (item.hasNotifications) {
                    vNotificationBadge.visible()
                } else {
                    vNotificationBadge.gone()
                }

                flNotificationBell.setOnClickListener { onNotificationClick() }
                ivUserAvatar.setOnClickListener { onProfileClick() }
            }
        }
    }

    class WelcomeViewHolder(
        private val binding: ItemHomeWelcomeBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: HomeItem.Welcome) {
            binding.tvWelcomeTitle.text = binding.root.context.getString(
                R.string.home_welcome_title,
                item.userName
            )
        }
    }

    class UpcomingEventsViewHolder(
        binding: ItemHomeUpcomingEventsBinding,
        onEventClick: (Int) -> Unit,
        private val onViewAllClick: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private val eventsAdapter = EventsAdapter(onEventClick)

        init {
            binding.rvUpcomingEvents.apply {
                adapter = eventsAdapter
                layoutManager = LinearLayoutManager(context)
            }

            binding.tvViewAll.setOnClickListener {
                onViewAllClick()
            }
        }

        fun bind(item: HomeItem.UpcomingEventsSection) {
            eventsAdapter.submitList(item.events)
        }
    }

    class CategoriesViewHolder(
        binding: ItemHomeCategoriesBinding,
        onCategoryClick: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private val categoriesAdapter = CategoriesAdapter(onCategoryClick)

        init {
            binding.rvCategories.apply {
                adapter = categoriesAdapter
                layoutManager = GridLayoutManager(context, 3)
            }
        }

        fun bind(item: HomeItem.CategoriesSection) {
            categoriesAdapter.submitList(item.categories)
        }
    }

    class TrendingViewHolder(
        binding: ItemHomeTrendingBinding,
        onEventClick: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private val trendingAdapter = TrendingEventsAdapter(onEventClick)

        init {
            binding.rvTrendingEvents.apply {
                adapter = trendingAdapter
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            }
        }

        fun bind(item: HomeItem.TrendingEventsSection) {
            trendingAdapter.submitList(item.events)
        }
    }

    private class HomeItemDiffCallback : DiffUtil.ItemCallback<HomeItem>() {
        override fun areItemsTheSame(oldItem: HomeItem, newItem: HomeItem): Boolean {
            return when {
                oldItem is HomeItem.Header && newItem is HomeItem.Header -> true
                oldItem is HomeItem.Welcome && newItem is HomeItem.Welcome -> true
                oldItem is HomeItem.UpcomingEventsSection && newItem is HomeItem.UpcomingEventsSection -> true
                oldItem is HomeItem.CategoriesSection && newItem is HomeItem.CategoriesSection -> true
                oldItem is HomeItem.TrendingEventsSection && newItem is HomeItem.TrendingEventsSection -> true
                else -> false
            }
        }

        override fun areContentsTheSame(oldItem: HomeItem, newItem: HomeItem): Boolean {
            return oldItem == newItem
        }
    }
}
