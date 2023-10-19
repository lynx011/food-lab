package com.example.food_test.view

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.food_test.R
import com.example.food_test.databinding.ActivityMainBinding
import com.example.food_test.databinding.FragmentSplashBinding
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var splashBinding: FragmentSplashBinding
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        splashBinding = FragmentSplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = findNavController(R.id.nav_host_fragment)
        NavigationUI.setupWithNavController(binding.bottomNav, navController)
//        binding.bottomNav.setOnItemReselectedListener {
//            when(it.itemId) {
//                R.id.homeFragment -> navController.navigate(R.id.homeFragment)
//                R.id.favouriteFragment -> navController.navigate(R.id.favouriteFragment)
//                R.id.categoryFragment -> navController.navigate(R.id.categoryFragment)
//            }
//        }

        visibilityBottomNav() // hide bottom nav in splash screen
        visibilityActionBar() // show and hide action bar

        Handler(Looper.getMainLooper()).postDelayed({
            findNavController(R.id.nav_host_fragment).popBackStack()
            findNavController(R.id.nav_host_fragment).navigate(R.id.loginFragment)
        }, 1000)
        splashBinding.splashProgress.isVisible = false

        getFirebaseDynamicLink()

        findNavController(R.id.nav_host_fragment).addOnDestinationChangedListener{_,d,_ ->
            when(d.id){
                R.id.bottomSheetFragment -> {
                    supportActionBar?.show()
                }
            }

        }
    }

    private fun getFirebaseDynamicLink() {
        FirebaseDynamicLinks.getInstance()
            .getDynamicLink(Intent())
            .addOnSuccessListener { pendingDynamicLinkData ->
                var deepLink : Uri? = null
                if(pendingDynamicLinkData != null){
                    deepLink = pendingDynamicLinkData.link
                }
                if(deepLink != null){
                    Log.d("loginActivity", "Dynamic links $deepLink")
                }
            }
    }

    private fun visibilityBottomNav() {
        findNavController(R.id.nav_host_fragment).addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.splashFragment -> {
                    binding.bottomNav.isVisible = false
                }

                R.id.loginFragment -> {
                    binding.bottomNav.isVisible = false
                }

                R.id.signUpFragment -> {
                    binding.bottomNav.isVisible = false
                }

                R.id.youtubeFragment -> {
                    binding.bottomNav.isVisible = false
                }

                R.id.randomMealDetailFragment -> {
                    binding.bottomNav.isVisible = false
                }

                else -> {
                    binding.bottomNav.isVisible = true
                }
            }
        }
    }

//    private fun visibilityBottomNavLogin() {
//        findNavController(R.id.nav_host_fragment).addOnDestinationChangedListener { _, destination, _ ->
//            when(destination.id) {
//                R.id.loginFragment -> {
//                    binding.bottomNav.visibility = View.GONE
//                }
//            }
//        }
//    }

    @SuppressLint("RestrictedApi")
    private fun visibilityActionBar() {
        findNavController(R.id.nav_host_fragment).addOnDestinationChangedListener { _, actionBar, _ ->
            when (actionBar.id) {
                R.id.splashFragment -> {
                    supportActionBar?.hide()
                }
                R.id.randomMealDetailFragment -> {
                    supportActionBar?.hide()
                }
                else -> {
                    supportActionBar?.show()
                }
            }
        }
    }

//    private var doubleBackToExitPressedOnce = false
//    override fun onBackPressed() {
//        if (doubleBackToExitPressedOnce) {
//            super.onBackPressed()
//            return finish()
//        }
//        this.doubleBackToExitPressedOnce = true
//        Handler(Looper.getMainLooper()).postDelayed({
//            findNavController(R.id.nav_host_fragment).navigate(R.id.homeFragment)
//        },1000)
//
//        Toast.makeText(this, "BackPressed again to exit", Toast.LENGTH_SHORT).show()
//        Handler(Looper.getMainLooper()).postDelayed({ doubleBackToExitPressedOnce = false }, 600)
//    }



}