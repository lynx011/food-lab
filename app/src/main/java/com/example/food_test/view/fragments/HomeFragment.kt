package com.example.food_test.view.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.food_test.R
import com.example.food_test.adapter.CategoryMealAdapter
import com.example.food_test.adapter.PopularMealAdapter
import com.example.food_test.api_service.ApiService
import com.example.food_test.databinding.FragmentHomeBinding
import com.example.food_test.repository.MealRepository
import com.example.food_test.view_model.MealViewModel
import com.example.food_test.view_model_factory.ViewModelFactory
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint


lateinit var mealViewModel: MealViewModel

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var popularMealAdapter: PopularMealAdapter
    private lateinit var categoryMealAdapter: CategoryMealAdapter
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("CommitPrefEdits", "InflateParams")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()

        val randomShimmer = binding.randomShimmerLayout // random card shimmer
        val popularShimmer = binding.popularShimmerLayout // popular card shimmer
        val retrofitService = ApiService.retrofitInstance()
        val mRepository = MealRepository(retrofitService)
        mealViewModel =
            ViewModelProvider(this, ViewModelFactory(mRepository))[MealViewModel::class.java]
        createPopularMealRecyclerView() // PopularMeal RecyclerView
        createCategoryRecyclerView() // Category RecyclerView
        mealViewModel.getRandomMeal()
        mealViewModel.getPopularMeal()
        mealViewModel.getMealCategories()

        mealViewModel.randomRandomMealsLiveData.observe(viewLifecycleOwner) { randomMeal ->
            randomShimmer.isVisible = true
            if (randomMeal != null) {
                Glide.with(this@HomeFragment)
                    .load(randomMeal.strMealThumb)
                    .into(binding.cardImg)
                randomShimmer.isVisible = false
            }


            binding.cardImg.setOnClickListener {
                val bundle = Bundle()
                val fragment = RandomMealDetailFragment()
                bundle.putString("randomImg", randomMeal.strMealThumb)
                bundle.putString("id", randomMeal.idMeal)
                bundle.putString("meal", randomMeal.strMeal)
                bundle.putString("area", randomMeal.strArea)
                bundle.putString("instructions", randomMeal.strInstructions)
                bundle.putString("utube", randomMeal.strYoutube)
                fragment.arguments = bundle

                findNavController().navigate(R.id.randomMealDetailFragment, bundle)
            }
        }

        mealViewModel.popularMealsLiveData.observe(viewLifecycleOwner) { popularMeal ->
            if (popularMeal != null) {
                popularMealAdapter.setPopularMealList(popularMeal)
                popularShimmer.isVisible = false
            } else {
                popularShimmer.isVisible = true
            }
        }
        val bottomSheetDialog = BottomSheetFragment()
        popularMealAdapter.onItemClick = { popular ->
            mealViewModel.getMealById(popular.idMeal)
            Handler(Looper.getMainLooper()).postDelayed({
                bottomSheetDialog.show(childFragmentManager, "BottomSheet")
            }, 500)
        }

        mealViewModel.getMealByIdLiveData.observe(viewLifecycleOwner) { btmMeal ->
//            bottomSheetDialog = BottomSheetFragment.newInstance(btmMeal.strMeal,btmMeal.strArea,btmMeal.strCategory,btmMeal.strMealThumb,btmMeal.strYoutube)
            val bundle = Bundle()
            bundle.apply {
                putString("mealName", btmMeal.strMeal)
                putString("mealArea", btmMeal.strArea)
                putString("mealCategory", btmMeal.strCategory)
                putString("mealImg", btmMeal.strMealThumb)
                putString("mealUtube", btmMeal.strYoutube)
                putString("mealInstruction", btmMeal.strInstructions)
            }
            bottomSheetDialog.arguments = bundle
        }


        mealViewModel.categoriesMealsLiveData.observe(viewLifecycleOwner) { categoryMeal ->
            if (categoryMeal != null) {
                categoryMealAdapter.setCategoryList(categoryMeal)

            }
        }

        categoryMealAdapter.onClick = { category ->

            Toast.makeText(requireContext(), category.strCategory, Toast.LENGTH_SHORT).show()
        }

    }

    private fun createPopularMealRecyclerView() {
        binding.recViewPopularMeal.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            popularMealAdapter = PopularMealAdapter()
            adapter = popularMealAdapter
        }
    }

    private fun createCategoryRecyclerView() {
        binding.recViewCategory.apply {
            layoutManager = GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false)
            categoryMealAdapter = CategoryMealAdapter()
            adapter = categoryMealAdapter
        }
    }


}