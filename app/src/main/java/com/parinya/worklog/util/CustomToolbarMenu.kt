package com.parinya.worklog.util

import android.view.MenuItem

interface CustomToolbarMenu {

    fun getOptionsMenu(): Int
    fun onOptionsMenuItemSelected(item: MenuItem)

}