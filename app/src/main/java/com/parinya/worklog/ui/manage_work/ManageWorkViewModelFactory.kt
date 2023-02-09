package com.parinya.worklog.ui.manage_work

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.parinya.worklog.db.work.WorkDao

class ManageWorkViewModelFactory(
    private val dao: WorkDao
) : ViewModelProvider.Factory {

    @RequiresApi(Build.VERSION_CODES.N)
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ManageHomeViewModel::class.java)) {
            return ManageHomeViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown view model class")
    }

}