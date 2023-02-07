package com.example.food_test.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.food_test.databinding.CategoryMealItemsBinding
import com.example.food_test.model.CategoryMeals

class CategoryMealAdapter() : RecyclerView.Adapter<CategoryMealAdapter.CategoryMealViewHolder>() {
    private val categoryList = mutableListOf<CategoryMeals>()

    @SuppressLint("NotifyDataSetChanged")
    fun setCategoryList(category: List<CategoryMeals>) {
        categoryList.clear()
        categoryList.addAll(category)
        notifyDataSetChanged()
    }

    var onClick: ((CategoryMeals) -> Unit)? = null

    class CategoryMealViewHolder(val categoryBinding: CategoryMealItemsBinding) :
        RecyclerView.ViewHolder(categoryBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryMealViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CategoryMealItemsBinding.inflate(inflater, parent, false)
        return CategoryMealViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryMealViewHolder, position: Int) {
        val categories = categoryList[position]
        holder.categoryBinding.apply {
            Glide.with(holder.itemView.context)
                .load(categories.strCategoryThumb)
                .into(categoryImg)
            categoryTitle.text = categories.strCategory
        }
        holder.itemView.setOnClickListener {
            onClick?.invoke(categories)
        }


    }

    override fun getItemCount(): Int {
        return categoryList.size
    }
}