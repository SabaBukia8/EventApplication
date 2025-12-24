package com.example.eventapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.eventapplication.databinding.ActivityMainBinding
import com.example.eventapplication.presentation.extensions.gone
import com.example.eventapplication.presentation.extensions.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    private val topLevelDestinations = setOf(
        R.id.homeFragment,
        R.id.browseFragment
    )

    private val authScreens = setOf(
        R.id.splashFragment,
        R.id.loginFragment,
        R.id.registerFragment
    )

    private val detailScreens = setOf(
        R.id.categoryEventsFragment,
        R.id.eventDetailsFragment
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupNavigation()
    }

    private fun setupNavigation() = with(binding) {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as androidx.navigation.fragment.NavHostFragment
        navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(topLevelDestinations)

        bottomNavigation.setupWithNavController(navController)

        bottomNavigation.setOnItemReselectedListener {}

        navController.addOnDestinationChangedListener { _, destination, _ ->
            updateBottomNavVisibility(destination)
        }
    }
    
    private fun updateBottomNavVisibility(destination: NavDestination) = with(binding) {
        when (destination.id) {
            in authScreens -> {
                bottomNavigation.gone()
            }
            in detailScreens -> {
                bottomNavigation.visible()
                bottomNavigation.menu.setGroupCheckable(0, false, true)
            }
            else -> {
                bottomNavigation.visible()
                bottomNavigation.menu.setGroupCheckable(0, true, true)
            }
        }
    }

    
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}