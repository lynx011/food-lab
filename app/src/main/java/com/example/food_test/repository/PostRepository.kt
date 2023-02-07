package com.example.food_test.repository

import com.example.food_test.api_service.PostApiService
import com.example.food_test.model.PostModelItem

class PostRepository(private val postApi: PostApiService) {

    suspend fun pushPost(post : PostModelItem) = postApi.pushPost(post)
}