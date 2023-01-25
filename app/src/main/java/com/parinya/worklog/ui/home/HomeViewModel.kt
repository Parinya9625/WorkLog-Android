package com.parinya.worklog.ui.home

import android.icu.util.Calendar
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parinya.worklog.db.Work
import com.parinya.worklog.db.WorkDao
import kotlinx.coroutines.launch
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

class HomeViewModel(
    private val dao: WorkDao
): ViewModel() {

    val works = dao.getWorks()

    @RequiresApi(Build.VERSION_CODES.N)
    fun addWork() {
        val work = Work(
            date = getCurrentDateInString(),
            timeIn = getCurrentTimeInString(),
        )
        viewModelScope.launch {
            dao.insertWork(work)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun getCurrentDateInString(): String {
        val date = Calendar.getInstance().time
        val formatter = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
        val dateStr = formatter.format(date)

        return dateStr
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun getCurrentTimeInString(): String {
        val time = Calendar.getInstance().time
        val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
        val timeStr = formatter.format(time)

        return timeStr
    }

    fun clearWorks() {
        viewModelScope.launch {
            dao.clearWorks()
        }
    }

    fun deleteWork(work: Work) {
        viewModelScope.launch {
            dao.deleteWork(work)
        }
    }
}