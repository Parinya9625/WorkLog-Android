package com.parinya.worklog.ui.work

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

class WorkViewModel(
    private val dao: WorkDao
): ViewModel() {

    // ========== Filter ==========

    val _sortedBy = MutableLiveData(HomeFilterSortedBy.None)
    val sortedBy: LiveData<HomeFilterSortedBy> = _sortedBy

    val _dateRange = MutableLiveData<String>()
    val dateRange: LiveData<String> = _dateRange

    val _pairDateRange = MutableLiveData<Pair<Long, Long>>()
    val pairDateRange: LiveData<Pair<Long, Long>> = _pairDateRange

    fun setSortedBy(filter: HomeFilterSortedBy) {
        _sortedBy.value = filter
    }

    fun getSortedBy(): HomeFilterSortedBy {
        return _sortedBy.value ?: HomeFilterSortedBy.None
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setDateRange(from: Calendar, to: Calendar) {
        val fromStr = Util.dateToString(from.timeInMillis, "dd MMM y")
        val toStr = Util.dateToString(to.timeInMillis, "dd MMM y")
        _dateRange.value = "$fromStr ➜ $toStr"

        _pairDateRange.value = Pair(
            from.timeInMillis,
            to.timeInMillis,
        )
    }

    fun clearFilter() {
        _sortedBy.value = HomeFilterSortedBy.None
        _dateRange.value = ""
        _pairDateRange.value = Pair(0, 0)
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