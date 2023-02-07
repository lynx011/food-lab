package com.example.food_test.view_model_factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.food_test.repository.MealRepository
import com.example.food_test.view_model.MealViewModel
import java.lang.IllegalArgumentException

class ViewModelFactory(private val mealRepository: MealRepository) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(MealViewModel::class.java)) {
            return MealViewModel(this.mealRepository) as T
        }
        else {
            throw IllegalArgumentException("something's wrong!")
        }
    }
}