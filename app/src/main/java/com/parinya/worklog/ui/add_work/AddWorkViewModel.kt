package com.parinya.worklog.ui.add_work

import android.content.Context
import android.icu.util.Calendar
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parinya.worklog.db.Work
import com.parinya.worklog.db.WorkDao
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

@RequiresApi(Build.VERSION_CODES.N)
class AddWorkViewModel(
    private val dao: WorkDao
): ViewModel() {

//    var work: Work = Work()

    val _date = MutableLiveData<String>(getCurrentDateInString())
    val date: LiveData<String> = _date

    val _timeIn = MutableLiveData<String>(getCurrentTimeInString())
    val timeIn: LiveData<String> = _timeIn

    val _timeOut = MutableLiveData<String>()
    val timeOut: LiveData<String> = _timeOut

    val _activity = MutableLiveData<String>()
    val activity: LiveData<String> = _activity

    val _knowledge = MutableLiveData<String>()
    val knowledge: LiveData<String> = _knowledge

    val _problem = MutableLiveData<String>()
    val problem: LiveData<String> = _problem


    private fun getCurrentDateInString(): String {
        val date = Calendar.getInstance().time
        val formatter = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
        val dateStr = formatter.format(date)

        return dateStr
    }

    private fun getCurrentTimeInString(): String {
        val time = Calendar.getInstance().time
        val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
        val timeStr = formatter.format(time)

        return timeStr
    }

    private fun getWork(): Work {
        val work = Work(
            date = date.value ?: "",
            timeIn = timeIn.value ?: "",
            timeOut = timeOut.value ?: "",
            activity = activity.value ?: "",
            knowledge = knowledge.value ?: "",
            problem = problem.value ?: "",
        )

        return work
    }

    fun saveWorkToDB() {
        val work = getWork()
        viewModelScope.launch {
            dao.insertWork(work)
        }
    }

    // Test
    fun showValue(context: Context) {
        val work = Work(
            date = date.value ?: "",
            timeIn = timeIn.value ?: "",
            timeOut = timeOut.value ?: "",
            activity = activity.value ?: "",
            knowledge = knowledge.value ?: "",
            problem = problem.value ?: "",
        )
        Toast.makeText(context, "${work}", Toast.LENGTH_LONG).show()
    }

}