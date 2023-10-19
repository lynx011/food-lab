package com.example.food_test.view.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.food_test.databinding.FragmentYoutubeBinding
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import dagger.hilt.android.AndroidEntryPoint
import java.util.regex.Pattern

@AndroidEntryPoint
class YoutubeFragment : Fragment() {
    lateinit var binding: FragmentYoutubeBinding
    lateinit var utubePlayerView: YouTubePlayerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentYoutubeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }


        val args = arguments
        val favCategory = args?.getString("cat")
        val favArea = args?.getString("ar")
        val favInstructions = args?.getString("instruct")
        val favUtube = args?.getString("youtube")

        Toast.makeText(requireContext(), favCategory.toString(), Toast.LENGTH_SHORT).show()
        if (favCategory != null && favArea != null && favInstructions != null) {
            binding.mealName.text = favCategory
            binding.mealArea.text = favArea
            binding.instructions.text = favInstructions
        }

        val randomCategory = args?.getString("category")
        val randomArea = args?.getString("area")
        val randomInstructions = args?.getString("instruction")
        val randomUtube = args?.getString("utube")

        if (randomCategory != null && randomArea != null && randomInstructions != null) {
            binding.mealName.text = randomCategory
            binding.mealArea.text = randomArea
            binding.instructions.text = randomInstructions

        }

        val btmMeal = args?.getString("btmMeal")
        val btmArea = args?.getString("btmArea")
        val btmInstruction = args?.getString("btmInstruction")
        val btmUtube = args?.getString("btmUtube")

        if (btmMeal != null && btmArea != null && btmInstruction != null) {
            binding.mealName.text = btmMeal
            binding.mealArea.text = btmArea
            binding.instructions.text = btmInstruction

        }


        utubePlayerView = binding.utubePlayerView
        lifecycle.addObserver(utubePlayerView)
        utubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(@NonNull youTubePlayer: YouTubePlayer) {

                val tracker = YouTubePlayerTracker()
                youTubePlayer.addListener(tracker)

                tracker.apply {
                    currentSecond
                    state
                    isVisible
                    videoId
                    videoDuration
                }

                utubePlayerView.apply {
                    enterFullScreen()
                    exitFullScreen()
                    enableBackgroundPlayback(true)
                }

                if (favUtube != null) {
                    val favVideoId = getYouTubeId(favUtube.toString())
                    if (favVideoId != null) {
                        youTubePlayer.loadVideo(favVideoId.toString(), 0f)
                    }
                }

                if (randomUtube != null) {
                    val randomVideoId = getYouTubeId(randomUtube.toString())
                    if (randomVideoId != null) {
                        youTubePlayer.loadVideo(randomVideoId.toString(), 0f)
                    }
                }

                if (btmUtube != null) {
                    val btmVideoId = getYouTubeId(btmUtube.toString())
                    if (btmVideoId != null) {
                        youTubePlayer.loadVideo(btmVideoId.toString(), 0f)
                    }
                }

            }
        })
    }


    private fun getYouTubeId(youTubeUrl: String): String? {
        val pattern = "(?<=youtu.be/|watch\\?v=|/videos/|embed/)[^#&?]*"
        val compiledPattern = Pattern.compile(pattern)
        val matcher = compiledPattern.matcher(youTubeUrl)
        return if (matcher.find()) {
            matcher.group()
        } else ({
            Toast.makeText(requireContext(), "not available", Toast.LENGTH_SHORT).show()
        }).toString()
    }

//    override fun onDestroyView() {
//        super.onDestroyView()
//        val shared = context?.getSharedPreferences("randomDetailPref",Context.MODE_PRIVATE)
//        val favShared = context?.getSharedPreferences("favMealSharedPref",Context.MODE_PRIVATE)
//        shared?.edit()?.clear()?.apply()
//        favShared?.edit()?.clear()?.apply()
//    }

    override fun onDestroy() {
        super.onDestroy()
        utubePlayerView.release()
    }

}