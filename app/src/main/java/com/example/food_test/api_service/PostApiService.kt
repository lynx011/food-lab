package com.example.food_test.api_service

import com.example.food_test.model.PostModel
import com.example.food_test.model.PostModelItem
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.POST

interface PostApiService {

    @POST("posts")
    suspend fun pushPost( @Body post : PostModelItem) : Response<PostModelItem>

    companion object {

        private const val baseURL = "https://jsonplaceholder.typicode.com/"
        fun retrofitInstance() : PostApiService {
            return Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(PostApiService::class.java)
        }
    }
}