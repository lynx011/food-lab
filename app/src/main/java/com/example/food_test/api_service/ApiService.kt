package com.example.food_test.api_service

import com.example.food_test.model.CategoryMealModel
import com.example.food_test.model.PopularMealModel
import com.example.food_test.model.RandomMealModel
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("random.php")
    suspend fun getRandomMeal() : Response<RandomMealModel>

    @GET("filter.php")
    suspend fun getPopularMeal(@Query("c") popularMealName : String) : Response<PopularMealModel>

    @GET("categories.php")
    suspend fun getMealCategories() : Response<CategoryMealModel>

    @GET("lookup.php")
    suspend fun getMealById(@Query("i") id : String) : Response<RandomMealModel>

    companion object {
        private const val baseUrl = "https://www.themealdb.com/api/json/v1/1/"
        fun retrofitInstance() : ApiService{
            return Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(ApiService::class.java)
        }
    }
}