package com.dicoding.nutrifact.ui

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.dicoding.nutrifact.R
import com.dicoding.nutrifact.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val navView: BottomNavigationView = binding.navView
        navView.setupWithNavController(navController)

//        binding.bubbleTabBar.addBubbleListener { id ->
//            when (id) {
//                R.id.navigation_home -> navController.navigate(R.id.navigation_home)
//                R.id.navigation_scan -> navController.navigate(R.id.navigation_scan)
//                R.id.navigation_profile -> navController.navigate(R.id.navigation_profile)
//            }
//        }
//
//        navController.addOnDestinationChangedListener { _, destination, _ ->
//            setSelectedWithId(destination.id)
//        }
//
//    }
//
//    private fun setSelectedWithId(destinationId: Int) {
//        when (destinationId) {
//            R.id.navigation_home -> binding.bubbleTabBar.setSelectedWithId(R.id.navigation_home)
//            R.id.navigation_scan -> binding.bubbleTabBar.setSelectedWithId(R.id.navigation_scan)
//            R.id.navigation_profile -> binding.bubbleTabBar.setSelectedWithId(R.id.navigation_profile)
//        }
    }

    override fun onBackPressed() {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        if (navController.currentDestination?.id in listOf(
                R.id.navigation_home,
                R.id.navigation_scan,
                R.id.navigation_profile
            )
        ) {
            finishAffinity()
        } else {
            super.onBackPressed()
        }
    }
}