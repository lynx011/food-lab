package com.example.food_test.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.food_test.model.CategoryMeals
import com.example.food_test.model.RandomMeals
import com.example.food_test.model.PopularMeals
import com.example.food_test.repository.MealRepository
import kotlinx.coroutines.*

class MealViewModel(private val mainMealRepository: MealRepository) : ViewModel() {

    var randomRandomMealsLiveData = MutableLiveData<RandomMeals>()
    var popularMealsLiveData = MutableLiveData<List<PopularMeals>?>()
    var categoriesMealsLiveData = MutableLiveData<List<CategoryMeals>>()
    var getMealByIdLiveData = MutableLiveData<RandomMeals>()
    private var loading = MutableLiveData<Boolean>()
    private var job: Job? = null
    private var errorMessage = MutableLiveData<String>()
    private val exception = CoroutineExceptionHandler { _, throwable ->
        onError("$throwable")
    }

    fun getRandomMeal() {
        loading.value = true
        job = CoroutineScope(Dispatchers.IO + exception).launch {
            val response = mainMealRepository.getRandomMeal()
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    randomRandomMealsLiveData.value = response.body()!!.meals[0]
                    loading.value = false
                } else {
                    onError(response.message())
                }
            }
        }
    }

    fun getMealById(mealId : String) {
        loading.value = true
        job = CoroutineScope(Dispatchers.IO + exception).launch {
            val response = mainMealRepository.getMealById(mealId)
            withContext(Dispatchers.Main){
                if(response.isSuccessful) {
                    getMealByIdLiveData.value = response.body()!!.meals[0]
                    loading.value = false
                }
                else{
                    onError(response.message())
                }
            }
        }
    }

    fun getPopularMeal() {
        loading.value = true
        job = CoroutineScope(Dispatchers.IO + exception).launch {
            val response = mainMealRepository.getPopularMeal()
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    popularMealsLiveData.value = response.body()!!.meals
                    loading.value = false
                } else {
                    onError(response.message())
                }
            }
        }
    }

    fun getMealCategories() {
        loading.value = true
        CoroutineScope(Dispatchers.IO + exception).launch {
            val response = mainMealRepository.getMealCategories()
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    categoriesMealsLiveData.value = response.body()!!.categories
                    loading.value = false
                } else {
                    onError(response.message())
                }
            }
        }
    }

    private fun onError(error: String) {
        errorMessage.value = error
        loading.value = false
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }

}