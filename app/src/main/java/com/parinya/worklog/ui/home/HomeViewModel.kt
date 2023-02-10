package com.parinya.worklog.ui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parinya.worklog.db.work.Work
import com.parinya.worklog.db.work.WorkDao
import com.parinya.worklog.util.Util
import kotlinx.coroutines.launch
import java.util.*

class HomeViewModel(
    private val dao: WorkDao
): ViewModel() {

    // ========== Filter ==========

    val sortedBy = MutableLiveData(HomeFilterSortedBy.None)
    val _sortedBy: LiveData<HomeFilterSortedBy> = sortedBy

    val dateRange = MutableLiveData<String>()
    val _dateRange: LiveData<String> = dateRange

    val pairDateRange = MutableLiveData<Pair<Long, Long>>()
    val _pairDateRange: LiveData<Pair<Long, Long>> = pairDateRange

    fun setSortedBy(filter: HomeFilterSortedBy) {
        sortedBy.value = filter
    }

    fun getSortedBy(): HomeFilterSortedBy {
        return _sortedBy.value ?: HomeFilterSortedBy.None
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setDateRange(from: Calendar, to: Calendar) {
        val fromStr = Util.dateToString(from.timeInMillis, "dd MMM y")
        val toStr = Util.dateToString(to.timeInMillis, "dd MMM y")
        dateRange.value = "$fromStr ➜ $toStr"

        pairDateRange.value = Pair(
            from.timeInMillis,
            to.timeInMillis,
        )
    }

    fun clearFilter() {
        sortedBy.value = HomeFilterSortedBy.None
        dateRange.value = ""
        pairDateRange.value = Pair(0, 0)
    }

    // ========== Database ==========

    fun deleteWork(work: Work) {
        viewModelScope.launch {
            dao.deleteWork(work)
        }
    }
}

enum class HomeFilterSortedBy {
    None,
    DateAsc,
    DateDes,
    Uncompleted
}