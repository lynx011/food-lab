package com.example.food_test.repository
import com.example.food_test.api_service.ApiService
import com.example.food_test.model.RandomMeals
import javax.inject.Inject

class MealRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getRandomMeal() = apiService.getRandomMeal()

    suspend fun getPopularMeal() = apiService.getPopularMeal("Dessert")

    suspend fun getMealCategories() = apiService.getMealCategories()

    suspend fun getMealById(id : String) = apiService.getMealById(id)

}