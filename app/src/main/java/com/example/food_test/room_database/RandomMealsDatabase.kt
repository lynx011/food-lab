package com.example.food_test.room_database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.food_test.model.RandomMeals

@Database(entities = [RandomMeals::class], version = 1, exportSchema = false)
@TypeConverters(MealTypeConverter::class)
abstract class RandomMealsDatabase : RoomDatabase() {

    abstract fun getRandomMealDao(): RandomMealDao

//    companion object {
//        @Volatile
//        private var instance: RandomMealsDatabase? = null
//
//        @Synchronized
//        fun getInstance(context: Context): RandomMealsDatabase {
//            if (instance == null) {
//                instance = Room.databaseBuilder(
//                    context.applicationContext,
//                    RandomMealsDatabase::class.java,
//                    "meals.DB"
//                )
//                    .build()
//            }
//            return instance as RandomMealsDatabase
//        }
//    }
}