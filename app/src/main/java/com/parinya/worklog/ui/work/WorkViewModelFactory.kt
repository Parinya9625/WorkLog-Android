package com.parinya.worklog.ui.work

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.parinya.worklog.db.work.WorkDao

class WorkViewModelFactory(
    private val dao: WorkDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WorkViewModel::class.java)) {
            return WorkViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown view model class")
    }

}