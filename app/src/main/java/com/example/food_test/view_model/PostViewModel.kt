package com.example.food_test.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.food_test.model.PostModelItem
import com.example.food_test.repository.PostRepository
import kotlinx.coroutines.launch

class PostViewModel(private val mainRepository: PostRepository) : ViewModel() {

    val postLiveData = MutableLiveData<PostModelItem>()

    fun pushPost(post : PostModelItem) = viewModelScope.launch {
        val response = mainRepository.pushPost(post)
        postLiveData.value = response.body()
    }
}