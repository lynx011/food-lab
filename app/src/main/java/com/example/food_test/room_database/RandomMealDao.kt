package com.example.food_test.room_database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.food_test.model.RandomMeals

@Dao
interface RandomMealDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeals(meal : RandomMeals)

    @Delete
    suspend fun deleteMeals(meal : RandomMeals)

    @Query("SELECT * FROM random_meals")
    fun getAllRandomMeals() : LiveData<List<RandomMeals>>

//    @Query("SELECT * FROM random_meals WHERE idMeal =:id")
//    fun getMealByID(id : String) : RandomMeals
//
//    @Query("DELETE FROM random_meals WHERE idMeal =:id")
//    fun deleteById(id : String) : RandomMeals

//    @Query("SELECT * FROM random_meals WHERE idMeal = :id")
//    fun getMealId(id: String): List<RandomMeals>
}