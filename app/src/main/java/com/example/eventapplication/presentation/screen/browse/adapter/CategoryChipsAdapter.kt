package com.example.eventapplication.presentation.screen.browse.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.eventapplication.R
import com.example.eventapplication.databinding.ItemCategoryChipBinding
import com.example.eventapplication.domain.model.Category
import com.example.eventapplication.presentation.screen.home.mapper.EventTypeMapper.toDisplayName

class CategoryChipsAdapter(
    private val onCategoryClick: (Int?) -> Unit
) : ListAdapter<CategoryChipsAdapter.CategoryItem, CategoryChipsAdapter.CategoryChipViewHolder>(CategoryDiffCallback()) {

    data class CategoryItem(
        val category: Category,
        val isSelected: Boolean
    )

    fun submitList(categories: List<Category>, selectedCategoryId: Int?) {
        val items = categories.map { category ->
            // Match if IDs match (including 0 for All)
            val isSelected = category.id == selectedCategoryId
            CategoryItem(category, isSelected)
        }
        super.submitList(items)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryChipViewHolder {
        val binding = ItemCategoryChipBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryChipViewHolder(binding, onCategoryClick)
    }

    override fun onBindViewHolder(holder: CategoryChipViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item.category, item.isSelected)
    }

    class CategoryChipViewHolder(
        private val binding: ItemCategoryChipBinding,
        private val onCategoryClick: (Int?) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(category: Category, isSelected: Boolean) {
            with(binding.chipCategory) {
                text = if (category.id == 0) {
                    context.getString(R.string.browse_all_events)
                } else {
                    category.type?.toDisplayName(context) ?: ""
                }

                setOnClickListener(null)

                isChecked = isSelected

                setOnClickListener {
                    onCategoryClick(category.id)
                }
            }
        }
    }

    private class CategoryDiffCallback : DiffUtil.ItemCallback<CategoryItem>() {
        override fun areItemsTheSame(oldItem: CategoryItem, newItem: CategoryItem): Boolean {
            return oldItem.category.id == newItem.category.id
        }

        override fun areContentsTheSame(oldItem: CategoryItem, newItem: CategoryItem): Boolean {
            return oldItem.category == newItem.category && oldItem.isSelected == newItem.isSelected
        }
    }
}