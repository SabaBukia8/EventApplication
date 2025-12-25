package com.example.eventapplication.presentation.screen.notifications

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.eventapplication.databinding.ItemNotificationBinding
import com.example.eventapplication.databinding.ItemNotificationEmptyBinding
import com.example.eventapplication.databinding.ItemNotificationHeaderBinding
import com.example.eventapplication.domain.model.Notification
import com.example.eventapplication.presentation.model.NotificationItem

class NotificationsAdapter(
    private val onNotificationClick: (Notification) -> Unit
) : ListAdapter<NotificationItem, RecyclerView.ViewHolder>(NotificationItemDiffCallback()) {

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_NOTIFICATION = 1
        private const val VIEW_TYPE_EMPTY = 2
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is NotificationItem.Header -> VIEW_TYPE_HEADER
            is NotificationItem.NotificationCard -> VIEW_TYPE_NOTIFICATION
            is NotificationItem.EmptyState -> VIEW_TYPE_EMPTY
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_HEADER -> HeaderViewHolder(
                ItemNotificationHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            VIEW_TYPE_NOTIFICATION -> NotificationViewHolder(
                ItemNotificationBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            VIEW_TYPE_EMPTY -> EmptyStateViewHolder(
                ItemNotificationEmptyBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is NotificationItem.Header -> (holder as HeaderViewHolder).bind(item)
            is NotificationItem.NotificationCard -> (holder as NotificationViewHolder).bind(item)
            is NotificationItem.EmptyState -> (holder as EmptyStateViewHolder).bind(item)
        }
    }

    inner class HeaderViewHolder(
        private val binding: ItemNotificationHeaderBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: NotificationItem.Header) {
            binding.tvHeaderTitle.text = item.displayText
        }
    }

    inner class NotificationViewHolder(
        private val binding: ItemNotificationBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: NotificationItem.NotificationCard) {
            binding.apply {
                tvTitle.text = item.notification.title
                tvMessage.text = item.notification.message
                tvTime.text = item.timeAgo
                ivIcon.setImageResource(item.iconRes)

                viewUnreadDot.isVisible = !item.notification.isRead

                root.setOnClickListener {
                    onNotificationClick(item.notification)
                }
            }
        }
    }

    inner class EmptyStateViewHolder(
        private val binding: ItemNotificationEmptyBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: NotificationItem.EmptyState) {
            binding.tvEmptyMessage.text = item.message
        }
    }
}

class NotificationItemDiffCallback : DiffUtil.ItemCallback<NotificationItem>() {
    override fun areItemsTheSame(oldItem: NotificationItem, newItem: NotificationItem): Boolean {
        return when {
            oldItem is NotificationItem.Header && newItem is NotificationItem.Header ->
                oldItem.dateCategory == newItem.dateCategory

            oldItem is NotificationItem.NotificationCard && newItem is NotificationItem.NotificationCard ->
                oldItem.notification.id == newItem.notification.id

            oldItem is NotificationItem.EmptyState && newItem is NotificationItem.EmptyState -> true
            else -> false
        }
    }

    override fun areContentsTheSame(oldItem: NotificationItem, newItem: NotificationItem): Boolean {
        return oldItem == newItem
    }
}
