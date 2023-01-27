package com.parinya.worklog

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.parinya.worklog.util.Util
import java.util.*

class SharedViewModel: ViewModel() {

    val sortedBy = MutableLiveData<FilterSortedBy>(FilterSortedBy.None)
    val _sortedBy: LiveData<FilterSortedBy> = sortedBy

    val dateRange = MutableLiveData<String>()
    val _dateRange: LiveData<String> = dateRange

    val pairDateRange = MutableLiveData<Pair<Long, Long>>()
    val _pairDateRange: LiveData<Pair<Long, Long>> = pairDateRange

    fun setSortedBy(filter: FilterSortedBy) {
        sortedBy.value = filter
    }

    fun getSortedBy(): FilterSortedBy {
        return _sortedBy.value ?: FilterSortedBy.None
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

    fun clear() {
        sortedBy.value = FilterSortedBy.None
        dateRange.value = ""
        pairDateRange.value = Pair(0, 0)
    }

}

enum class FilterSortedBy {
    None,
    DateAsc,
    DateDes,
    Uncompleted
}