package com.example.eventapplication.presentation.screen.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.eventapplication.R
import com.example.eventapplication.databinding.ItemCategoryCardBinding
import com.example.eventapplication.domain.model.Category
import com.example.eventapplication.presentation.screen.home.mapper.EventTypeMapper.toDisplayName
import com.example.eventapplication.presentation.screen.home.mapper.EventTypeMapper.toIconRes

class CategoriesAdapter(
    private val onCategoryClick: (Int) -> Unit
) : ListAdapter<Category, CategoriesAdapter.CategoryViewHolder>(CategoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(binding, onCategoryClick)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CategoryViewHolder(
        private val binding: ItemCategoryCardBinding,
        private val onCategoryClick: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(category: Category) {
            with(binding) {
                category.type?.let { type ->
                    ivCategoryIcon.setImageResource(type.toIconRes())
                }

                tvCategoryName.text = category.type?.toDisplayName(root.context) ?: ""

                tvEventCount.text = root.context.getString(
                    R.string.category_events_count,
                    category.eventCount
                )

                root.setOnClickListener {
                    onCategoryClick(category.id)
                }
            }
        }
    }

    private class CategoryDiffCallback : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem == newItem
        }
    }
}
