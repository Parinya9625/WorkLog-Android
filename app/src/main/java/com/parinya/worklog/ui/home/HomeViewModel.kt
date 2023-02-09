package com.parinya.worklog.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parinya.worklog.db.work.Work
import com.parinya.worklog.db.work.WorkDao
import kotlinx.coroutines.launch

class HomeViewModel(
    private val dao: WorkDao
): ViewModel() {

    fun deleteWork(work: Work) {
        viewModelScope.launch {
            dao.deleteWork(work)
        }
    }
}