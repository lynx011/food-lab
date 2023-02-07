package com.example.food_test.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.food_test.model.RandomMeals
import com.example.food_test.repository.FavMealRepository
import com.example.food_test.room_database.RandomMealsDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavMealViewModel(private val favMealRepository : FavMealRepository) : ViewModel(){

    val favMealsLiveData = favMealRepository.allMealItems()

    fun insert(meal : RandomMeals) = viewModelScope.launch {
        favMealRepository.insertMeal(meal)
    }
    fun delete(meal : RandomMeals) = viewModelScope.launch {
        favMealRepository.deleteMeal(meal)
    }

    fun getAllMeals() = favMealRepository.allMealItems()

}