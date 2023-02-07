package com.example.food_test.view_model_factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.food_test.repository.FavMealRepository
import com.example.food_test.room_database.RandomMealsDatabase
import com.example.food_test.view_model.FavMealViewModel

class FavViewModelFactory(private val repository: FavMealRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FavMealViewModel(repository) as T
    }
}