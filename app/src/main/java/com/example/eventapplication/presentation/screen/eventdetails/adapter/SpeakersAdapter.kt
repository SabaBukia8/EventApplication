package com.example.eventapplication.presentation.screen.eventdetails.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.eventapplication.databinding.ItemSpeakerBinding
import com.example.eventapplication.domain.model.Speaker

class SpeakersAdapter : ListAdapter<Speaker, SpeakersAdapter.SpeakerViewHolder>(SpeakerDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpeakerViewHolder {
        val binding = ItemSpeakerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SpeakerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SpeakerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class SpeakerViewHolder(
        private val binding: ItemSpeakerBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(speaker: Speaker) {
            with(binding) {
                tvName.text = speaker.name
                tvTitle.text = speaker.title
                // TODO: Load avatar image if avatarUrl is not null using Coil/Glide
            }
        }
    }

    private class SpeakerDiffCallback : DiffUtil.ItemCallback<Speaker>() {
        override fun areItemsTheSame(oldItem: Speaker, newItem: Speaker): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Speaker, newItem: Speaker): Boolean {
            return oldItem == newItem
        }
    }
}
