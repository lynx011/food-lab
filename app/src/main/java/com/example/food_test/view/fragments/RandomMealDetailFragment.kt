package com.example.food_test.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.food_test.R
import com.example.food_test.databinding.FragmentRandomMealDetailBinding
import com.example.food_test.repository.FavMealRepository
import com.example.food_test.room_database.RandomMealsDatabase
import com.example.food_test.view_model.FavMealViewModel
import com.example.food_test.view_model_factory.FavViewModelFactory

lateinit var favViewModel: FavMealViewModel
var isClicked = false

class RandomMealDetailFragment : Fragment() {
    private lateinit var binding: FragmentRandomMealDetailBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRandomMealDetailBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        isClicked = false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = arguments
        val database = RandomMealsDatabase.getInstance(requireContext())
        val repository = FavMealRepository(database)
        val favFactory = FavViewModelFactory(repository)
        favViewModel = ViewModelProvider(this, favFactory)[FavMealViewModel::class.java]

        val randomMealImg = args?.getString("randomImg")
        val randomCategory = args?.getString("meal")
        val randomArea = args?.getString("area")
        val randomInstructions = args?.getString("instructions")
        val randomUtube = args?.getString("utube")

        if (randomMealImg != null) {
            Glide.with(this)
                .load(randomMealImg)
                .placeholder(R.drawable.img_6)
                .into(binding.randomMealDetailImg)
        }
        binding.detailBackKey.setOnClickListener {
            it.findNavController().popBackStack()
        }
        binding.category.text = randomCategory
        binding.area.text = randomArea
        binding.instructions.text = randomInstructions
        binding.utube.setOnClickListener {
//            val uri = Uri.parse(randomUtube)
//            val intent = Intent(Intent.ACTION_VIEW)
//            intent.data = uri
//            context?.startActivity(intent)
            val bundle = bundleOf(
                Pair("category", randomCategory),
                Pair("area", randomArea),
                Pair("instruction", randomInstructions),
                Pair("utube", randomUtube)
            )
            findNavController().navigate(R.id.youtubeFragment, bundle)

        }

        mealViewModel.randomRandomMealsLiveData.observe(viewLifecycleOwner) { randomMeals ->
            binding.favBtn.setOnClickListener {
                when (it.isSelected) {
                    !isClicked -> {
                        binding.favBtn.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_favorite_border
                            )
                        )
                        favViewModel.delete(randomMeals)
                        Toast.makeText(requireContext(), "UnSaved", Toast.LENGTH_SHORT).show()
                        isClicked = false
                    }
                    isClicked -> {
                        binding.favBtn.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.favorite
                            )
                        )
                        favViewModel.insert(randomMeals)
                        Toast.makeText(requireContext(), "Saved", Toast.LENGTH_SHORT).show()
                        isClicked = true
                    }
                    else -> Toast.makeText(
                        requireContext(),
                        "can't save",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }

        val favMealName = args?.getString("favName")
        val favMealArea = args?.getString("favArea")
        val favMealThumb = args?.getString("favThumb")
        val favMealInstruction = args?.getString("favInstruction")
        val favMealUtube = args?.getString("favUtube")

        if (favMealName != null && favMealArea != null && favMealInstruction != null && favMealUtube != null) {
            binding.apply {
                category.text = favMealName.toString()
                area.text = favMealArea.toString()
                instructions.text = favMealInstruction.toString()
                Glide.with(this@RandomMealDetailFragment)
                    .load(favMealThumb)
                    .into(randomMealDetailImg)
                utube.setOnClickListener {
                    val bundle = bundleOf(
                        Pair("cat", favMealName),
                        Pair("ar", favMealArea),
                        Pair("instruct", favMealInstruction),
                        Pair("youtube", favMealUtube)
                    )
                    findNavController().navigate(R.id.youtubeFragment, bundle)
                }
            }

        }

        val btmMeal = args?.getString("bName")
        val btmArea = args?.getString("bArea")
        val btmThumb = args?.getString("bThumb")
        val btmInstruction = args?.getString("bInstruction")
        val btmUtube = args?.getString("bUtube")

        if (btmMeal != null && btmArea != null && btmThumb != null && btmUtube != null && btmInstruction != null) {
            binding.apply {
                category.text = btmMeal.toString()
                area.text = btmArea.toString()
                instructions.text = btmInstruction.toString()
                Glide.with(this@RandomMealDetailFragment)
                    .load(btmThumb)
                    .into(randomMealDetailImg)
                utube.setOnClickListener {
                    val bundle = bundleOf(
                        Pair("btmMeal", btmMeal),
                        Pair("btmArea", btmArea),
                        Pair("btmInstruction", btmInstruction),
                        Pair("btmUtube", btmUtube)
                    )
                    findNavController().navigate(R.id.youtubeFragment, bundle)
                }
            }

        }
    }

}