package com.parinya.worklog

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.parinya.worklog.ui.work.WorkFragmentDirections

class MainActivity : AppCompatActivity() {

    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    private lateinit var sharedViewModel: SharedViewModel
    private val rootFragments = setOf(
        R.id.workFragment, R.id.noteFragment
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupNavHostAndNavController()
        setupBottomNavBar()
        handleShortcutIntent()

        sharedViewModel = ViewModelProvider(this)[SharedViewModel::class.java]

    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun handleShortcutIntent() {
        when (intent.action) {
            applicationContext.getString(R.string.intent_shortcut_addwork) -> {
                val action = WorkFragmentDirections.actionHomeFragmentToManageHomeFragment()
                navController.navigate(action)
            }
        }
    }

    private fun setupNavHostAndNavController() {
        navHostFragment = supportFragmentManager.findFragmentById(R.id.navHost) as NavHostFragment
        navController = navHostFragment.findNavController()

        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            isBottomNavBarVisible(destination.id)
        }
    }

    private fun isBottomNavBarVisible(fragmentId: Int) {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationBar)
        bottomNav.isVisible = rootFragments.contains(fragmentId)
    }

    private fun setupBottomNavBar() {
        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottomNavigationBar)
        bottomNavigation.setupWithNavController(navController)
    }

}