package com.parinya.worklog.ui.edit_work

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.parinya.worklog.db.WorkDao

class EditWorkViewModelFactory(
    private val dao: WorkDao
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditWorkViewModel::class.java)) {
            return EditWorkViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown view model class")
    }

}