package com.example.food_test.view_model_factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.food_test.repository.PostRepository
import com.example.food_test.view_model.PostViewModel

class PostViewModelFactory(private val repository: PostRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PostViewModel(repository) as T
    }
}