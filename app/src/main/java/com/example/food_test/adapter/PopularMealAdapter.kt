package com.example.food_test.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.food_test.databinding.PopularMealItemsBinding
import com.example.food_test.model.CategoryMeals
import com.example.food_test.model.PopularMeals

class PopularMealAdapter() : RecyclerView.Adapter<PopularMealAdapter.PopularMealViewHolder>() {

    private var popularMealList = mutableListOf<PopularMeals>()
    @SuppressLint("NotifyDataSetChanged")
    fun setPopularMealList(meal : List<PopularMeals>) {
        popularMealList.addAll(meal)
        notifyDataSetChanged()
    }
    var onItemClick : ((PopularMeals) -> Unit)? = null

    class PopularMealViewHolder(val itemBinding : PopularMealItemsBinding) :
        RecyclerView.ViewHolder(itemBinding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularMealViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PopularMealItemsBinding.inflate(inflater,parent,false)
        return  PopularMealViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PopularMealViewHolder, position: Int) {
        val popularMeal = popularMealList[position]
        holder.itemBinding.apply {
            Glide.with(holder.itemView.context)
                .load(popularMeal.strMealThumb)
                .into(popularImg)
            popularTitle.text = popularMeal.strMeal
        }
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(popularMeal)
        }
    }

    override fun getItemCount(): Int {
       return popularMealList.size
    }
}