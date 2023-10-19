package com.example.food_test.view.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.food_test.R
import com.example.food_test.databinding.FragmentBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class BottomSheetFragment : BottomSheetDialogFragment() {
    lateinit var binding: FragmentBottomSheetBinding
    private var mealId: String? = null
    private var mealName: String? = null
    private var mealArea: String? = null
    private var mealCategory: String? = null
    private var mealImg: String? = null
    private var mealUtube: String? = null
    private var mealInstruction: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mealId = it.getString("mealId")
            mealName = it.getString("mealName")
            mealArea = it.getString("mealArea")
            mealCategory = it.getString("mealCategory")
            mealImg = it.getString("mealImg")
            mealUtube = it.getString("mealUtube")
            mealInstruction = it.getString("mealInstruction")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBottomSheetBinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint("CommitPrefEdits")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            bMeal.text = mealName.toString()
            bArea.text = mealArea.toString()
            bCategory.text = mealCategory.toString()
            Glide.with(this@BottomSheetFragment)
                .load(mealImg)
                .into(bMealImg)
        }

        binding.bReadMore.setOnClickListener {
            val bundle = Bundle()
            bundle.apply {
                putString("bName", mealName)
                putString("bArea", mealArea)
                putString("bThumb", mealImg)
                putString("bUtube", mealUtube)
                putString("bInstruction", mealInstruction)
            }
            findNavController().navigate(R.id.randomMealDetailFragment, bundle)
            this.dismiss()
        }
    }

}