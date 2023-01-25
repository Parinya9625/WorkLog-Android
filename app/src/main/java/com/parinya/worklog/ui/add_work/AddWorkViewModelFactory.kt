package com.parinya.worklog.ui.add_work

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.parinya.worklog.db.WorkDao

class AddWorkViewModelFactory(
    private val dao: WorkDao
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddWorkViewModel::class.java)) {
            return AddWorkViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown view model class")
    }

}