package com.parinya.worklog.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parinya.worklog.db.work.Work
import com.parinya.worklog.db.work.WorkDao
import kotlinx.coroutines.launch

class SearchViewModel(
    private val dao: WorkDao
) : ViewModel() {

    val searchQuery = MutableLiveData<String>()
    val _searchQuery: LiveData<String> = searchQuery

    val searchResult = MutableLiveData<List<Work>>()
    val _searchResult: LiveData<List<Work>> = searchResult

    fun deleteWork(work: Work) {
        viewModelScope.launch {
            dao.deleteWork(work)
        }
    }
}