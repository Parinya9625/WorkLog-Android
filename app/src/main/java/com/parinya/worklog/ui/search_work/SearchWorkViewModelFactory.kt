package com.parinya.worklog.ui.search_work

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.parinya.worklog.db.work.WorkDao

class SearchWorkViewModelFactory(
    private val dao: WorkDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchWorkViewModel::class.java)) {
            return SearchWorkViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknow view model class")
    }
}