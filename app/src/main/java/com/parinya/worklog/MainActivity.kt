package com.parinya.worklog

import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.parinya.worklog.databinding.FilterSheetBinding
import com.parinya.worklog.ui.home.HomeFragmentDirections
import com.parinya.worklog.util.Util
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var sharedViewModel: SharedViewModel
    lateinit var mainMenu: Menu
    private var showOptionMenu = true

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHost) as NavHostFragment
        navController = navHostFragment.findNavController()

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        // Top level fragment (don't show back arrow)
        val appBarConfig = AppBarConfiguration(setOf(
            R.id.homeFragment, R.id.noteFragment
        ))
        setSupportActionBar(toolbar)
        setupActionBarWithNavController(navController, appBarConfig)

        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottomNavigationBar)
        bottomNavigation.setupWithNavController(navController)

        sharedViewModel = ViewModelProvider(this)[SharedViewModel::class.java]

        val intentAction = intent.action
        if (intentAction.equals("com.parinya.worklog.addwork", true)) {
            val action = HomeFragmentDirections.actionHomeFragmentToManageHomeFragment()
            navController.navigate(action)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        if (menu != null) {
            mainMenu = menu
        }

        menu?.children?.forEach {
            it.isVisible = showOptionMenu
        }

        return true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.toString()) {
            "Filter" -> filterBottomSheetDialog()
            "Search" -> {
                val action = HomeFragmentDirections.actionHomeFragmentToSearchFragment()
                navController.navigate(action)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    fun setOptionsMenuVisible(value: Boolean) {
        showOptionMenu = value
        invalidateOptionsMenu()
    }

    fun setBottomNavigationBarVisible(value: Boolean) {
        val bottomNavBar = findViewById<BottomNavigationView>(R.id.bottomNavigationBar)
        bottomNavBar.isVisible = value
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun filterBottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val sheetBinding = FilterSheetBinding.inflate(layoutInflater, null, false)
        sheetBinding.lifecycleOwner = this
        sheetBinding.viewModel = sharedViewModel

        val textInputLayout = sheetBinding.ipfDateRange
        Util.convertInputToDateRangePicker(
            supportFragmentManager,
            textInputLayout,
            onDateRangeSet = {from, to ->
                sharedViewModel.setDateRange(from, to)
            }
        )

        when (sharedViewModel.getSortedBy()) {
            FilterSortedBy.DateAsc -> sheetBinding.filterSortByGroup.check(R.id.cfDateAscending)
            FilterSortedBy.DateDes -> sheetBinding.filterSortByGroup.check(R.id.cfDateDescending)
            FilterSortedBy.Uncompleted -> sheetBinding.filterSortByGroup.check(R.id.cfUncompleted)
            else -> {}
        }

        sheetBinding.filterSortByGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            when (checkedIds.firstOrNull()) {
                null -> sharedViewModel.setSortedBy(FilterSortedBy.None)
                R.id.cfDateAscending -> sharedViewModel.setSortedBy(FilterSortedBy.DateAsc)
                R.id.cfDateDescending -> sharedViewModel.setSortedBy(FilterSortedBy.DateDes)
                R.id.cfUncompleted -> sharedViewModel.setSortedBy(FilterSortedBy.Uncompleted)
            }
        }

        sheetBinding.btnClear.setOnClickListener {
            sharedViewModel.clear()
            sheetBinding.filterSortByGroup.clearCheck()
        }

        sheetBinding.btnDone.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.setContentView(sheetBinding.root)
        bottomSheetDialog.show()
    }
}