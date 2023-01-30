package com.parinya.worklog.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.parinya.worklog.db.WorkDao

class SearchViewModelFactory(
    private val dao: WorkDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknow view model class")
    }
}