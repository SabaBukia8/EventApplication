// In eventapplication/presentation/eventdetails/adapter/nested/AgendaAdapter.kt

package com.example.eventapplication.presentation.screen.eventdetails.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.eventapplication.databinding.ItemAgendaItemBinding
import com.example.eventapplication.domain.model.AgendaItem

class AgendaAdapter : ListAdapter<AgendaItem, AgendaAdapter.AgendaViewHolder>(AgendaDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AgendaViewHolder {
        val binding = ItemAgendaItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AgendaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AgendaViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class AgendaViewHolder(
        private val binding: ItemAgendaItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(agendaItem: AgendaItem) {
            with(binding) {
                tvStepNumber.text = agendaItem.step.toString()
                
                tvTitle.text = binding.root.context.getString(
                    com.example.eventapplication.R.string.agenda_item_format,
                    agendaItem.time,
                    agendaItem.title
                )

                tvDescription.text = agendaItem.description
                
                val isLast = bindingAdapterPosition == this@AgendaAdapter.itemCount - 1
                
                vVerticalLine.visibility = if (isLast) View.GONE else View.VISIBLE
            }
        }
    }

    private class AgendaDiffCallback : DiffUtil.ItemCallback<AgendaItem>() {
        override fun areItemsTheSame(oldItem: AgendaItem, newItem: AgendaItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: AgendaItem, newItem: AgendaItem): Boolean {
            return oldItem == newItem
        }
    }
}