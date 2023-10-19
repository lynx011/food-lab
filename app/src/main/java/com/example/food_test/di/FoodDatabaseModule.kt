package com.example.food_test.di

import android.content.Context
import androidx.room.Room
import androidx.room.
import com.example.food_test.room_database.RandomMealDao
import com.example.food_test.room_database.RandomMealsDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FoodDatabaseModule {

    @Provides
    @Singleton
    fun provideMealsDatabase(@ApplicationContext context: Context) : RandomMealsDatabase =
        Room.databaseBuilder(context,RandomMealsDatabase::class.java,"meals.DB")
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()

    @Provides
    @Singleton
    fun provideMealsDao(mealsDatabase: RandomMealsDatabase) : RandomMealDao =
        mealsDatabase.getRandomMealDao()

}