package com.example.food_test.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.food_test.R
import com.example.food_test.databinding.FavMealsItemsBinding
import com.example.food_test.model.RandomMeals

@SuppressLint("StaticFieldLeak")

class FavMealAdapter : RecyclerView.Adapter<FavMealAdapter.FavMealViewHolder>() {
    lateinit var binding: FavMealsItemsBinding
    var onItemClick: ((RandomMeals) -> Unit)? = null
    var onClick : ((RandomMeals) -> Unit)? = null
    class FavMealViewHolder(val favMealBinding: FavMealsItemsBinding) :
        RecyclerView.ViewHolder(favMealBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavMealViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = FavMealsItemsBinding.inflate(inflater, parent, false)
        return FavMealViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavMealViewHolder, position: Int) {
        val favMeals = differ.currentList[position]
        holder.favMealBinding.apply {
            Glide.with(holder.itemView)
                .load(favMeals.strMealThumb)
                .into(favMealImg)

            favMealTv.text = favMeals.strMeal
            holder.itemView.setOnClickListener {
                onClick?.invoke(favMeals)
            }
            favIcon.setOnClickListener {
                onItemClick?.invoke(favMeals)
                favIcon.setImageDrawable(
                    ContextCompat.getDrawable(
                        holder.itemView.context,
                        R.drawable.ic_favorite_border
                    )
                )
            }
        }

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private val diffUtil = object : DiffUtil.ItemCallback<RandomMeals>() {

        override fun areItemsTheSame(oldItem: RandomMeals, newItem: RandomMeals): Boolean {
            return oldItem.idMeal == newItem.idMeal
        }

        override fun areContentsTheSame(oldItem: RandomMeals, newItem: RandomMeals): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this, diffUtil)
}