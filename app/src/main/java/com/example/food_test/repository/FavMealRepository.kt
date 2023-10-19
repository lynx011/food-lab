package com.example.food_test.repository

import com.example.food_test.model.RandomMeals
import com.example.food_test.room_database.RandomMealsDatabase
import javax.inject.Inject

class FavMealRepository @Inject constructor(private val db: RandomMealsDatabase) {

    suspend fun insertMeal(meal: RandomMeals) = db.getRandomMealDao().insertMeals(meal)

    suspend fun deleteMeal(meal: RandomMeals) = db.getRandomMealDao().deleteMeals(meal)

    fun allMealItems() = db.getRandomMealDao().getAllRandomMeals()

//    fun getMealById(mealId:String) = db.getRandomMealDao().getMealByID(mealId)
//    fun deleteById(id:String) = db.getRandomMealDao().deleteById(id)

}