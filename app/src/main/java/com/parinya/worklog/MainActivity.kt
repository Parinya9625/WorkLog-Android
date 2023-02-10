package com.parinya.worklog

import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.parinya.worklog.ui.home.HomeFragmentDirections
import com.parinya.worklog.util.CustomToolbarMenu
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupNavHostAndNavController()
        setupToolbar()
        setupBottomNavBar()
        handleShortcutIntent()

        sharedViewModel = ViewModelProvider(this)[SharedViewModel::class.java]

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val currentFragment = navHostFragment.childFragmentManager.fragments.first()

        var optionsMenu = R.menu.blank_menu
        if (currentFragment is CustomToolbarMenu) {
            optionsMenu = currentFragment.getOptionsMenu()
        }

        menuInflater.inflate(optionsMenu, menu)
        return true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val currentFragment = navHostFragment.childFragmentManager.fragments.first()

        if (currentFragment is CustomToolbarMenu) {
            currentFragment.onOptionsMenuItemSelected(item)
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun handleShortcutIntent() {
        when (intent.action) {
            applicationContext.getString(R.string.intent_shortcut_addwork) -> {
                val action = HomeFragmentDirections.actionHomeFragmentToManageHomeFragment()
                navController.navigate(action)
            }
        }
    }

    private fun setupNavHostAndNavController() {
        navHostFragment = supportFragmentManager.findFragmentById(R.id.navHost) as NavHostFragment
        navController = navHostFragment.findNavController()

        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            invalidateOptionsMenu()
        }
    }

    private fun setupToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        // Top level fragment (don't show back arrow)
        val appBarConfig = AppBarConfiguration(setOf(
            R.id.homeFragment, R.id.noteFragment
        ))
        setSupportActionBar(toolbar)
        setupActionBarWithNavController(navController, appBarConfig)
    }

    private fun setupBottomNavBar() {
        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottomNavigationBar)
        bottomNavigation.setupWithNavController(navController)
    }

}