package com.example.food_test.view.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.food_test.R
import com.example.food_test.adapter.FavMealAdapter
import com.example.food_test.databinding.FragmentFavouriteBinding
import com.example.food_test.repository.FavMealRepository
import com.example.food_test.room_database.RandomMealsDatabase
import com.example.food_test.view_model.FavMealViewModel
import com.example.food_test.view_model_factory.FavViewModelFactory
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavouritesFragment : Fragment() {
    private lateinit var binding: FragmentFavouriteBinding
    private lateinit var favMealAdapter: FavMealAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavouriteBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("ResourceAsColor", "ResourceType", "CommitPrefEdits")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val database = RandomMealsDatabase.getInstance(requireContext())
        val repository = FavMealRepository(database)
        val favFactory = FavViewModelFactory(repository)
        favViewModel = ViewModelProvider(this, favFactory)[FavMealViewModel::class.java]

        binding.favMealsRec.apply {
            layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
            favMealAdapter = FavMealAdapter()
            adapter = favMealAdapter
        }

        favViewModel.favMealsLiveData.observe(viewLifecycleOwner) { favMeals ->
            favMealAdapter.differ.submitList(favMeals)

            if (favMeals.isEmpty()) {
                binding.favNotFound.isVisible = true
            }
            if (favMeals.isNotEmpty()) {
                binding.favNotFound.isVisible = false

            }

        }


        favMealAdapter.onClick = { favMeals ->
//            val favMealSharePref =
//                context?.getSharedPreferences("favMealSharedPref", Context.MODE_PRIVATE)
//            val favMealEditor = favMealSharePref?.edit()
//            favMealEditor?.apply {
//                putString("favMeal", favMeals.strMeal)
//                putString("favArea", favMeals.strArea)
//                putString("favInstructions", favMeals.strInstructions)
//                putString("favUtube", favMeals.strYoutube)
//                apply()
//            }
//            findNavController().navigate(R.id.randomMealDetailFragment)
            val bundle = Bundle()
            val detailFragment = RandomMealDetailFragment()
            bundle.putString("favName", favMeals.strMeal)
            bundle.putString("favArea", favMeals.strArea)
            bundle.putString("favThumb",favMeals.strMealThumb)
            bundle.putString("favInstruction", favMeals.strInstructions)
            bundle.putString("favUtube", favMeals.strYoutube)
            detailFragment.arguments = bundle
            findNavController().navigate(R.id.randomMealDetailFragment, bundle)
        }

        favMealAdapter.onItemClick = { favMeals ->
            Toast.makeText(requireContext(), "Unsaved", Toast.LENGTH_SHORT).show()
            Handler(Looper.getMainLooper()).postDelayed({
                favViewModel.delete(favMeals)
            }, 500)
        }
    }
}

//val snackBar = Snackbar.make(
//    requireView(),
//    "Successfully Removed ${favMeals.idMeal}",
//    Snackbar.LENGTH_LONG,
//).setAction("Cancel") { view ->
//    favViewModel.insert(favMeals)
//    view.elevation = 60F
//}.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
//    .setActionTextColor(ContextCompat.getColor(requireContext(), R.color.deep_green))
//    .setAnimationMode(ANIMATION_MODE_SLIDE).setTextMaxLines(2)
//    .setDuration(3000)
//snackBar.show()