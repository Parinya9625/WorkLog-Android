package com.parinya.worklog.ui.manage_work

import android.icu.util.Calendar
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.datepicker.MaterialDatePicker
import com.parinya.worklog.db.work.Work
import com.parinya.worklog.db.work.WorkDao
import com.parinya.worklog.util.Util
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
class ManageHomeViewModel(
    private val dao: WorkDao
): ViewModel() {

    var _work: Work = Work()

    val _date = MutableLiveData<String>(getCurrentDateInString())
    val date: LiveData<String> = _date

    val _dateDB = MutableLiveData<Long>(MaterialDatePicker.todayInUtcMilliseconds())
    val dateDB: LiveData<Long> = _dateDB

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
        val formatter = SimpleDateFormat("dd MMMM y", Locale.getDefault())
        val dateStr = formatter.format(date)

        return dateStr
    }

    private fun getCurrentTimeInString(): String {
        val time = Calendar.getInstance().time
        val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
        val timeStr = formatter.format(time)

        return timeStr
    }

    fun setWork(work: Work) {
        _work = work

        _date.value = Util.dateToString(work.date)
        _dateDB.value = work.date
        _timeIn.value = work.timeIn
        _timeOut.value = work.timeOut
        _activity.value = work.activity
        _knowledge.value = work.knowledge
        _problem.value = work.problem
    }

    private fun getAddWork(): Work {
        return Work(
            date = dateDB.value ?: 0L,
            timeIn = timeIn.value ?: "",
            timeOut = timeOut.value ?: "",
            activity = activity.value ?: "",
            knowledge = knowledge.value ?: "",
            problem = problem.value ?: "",
        )
    }

    private fun getUpdateWork(): Work {
        return Work(
            id = _work.id,
            viewType = _work.viewType,
            date = dateDB.value ?: 0L,
            timeIn = timeIn.value ?: "",
            timeOut = timeOut.value ?: "",
            activity = activity.value ?: "",
            knowledge = knowledge.value ?: "",
            problem = problem.value ?: "",
        )
    }

    fun setDate(inString: String, inMS: Long) {
        _date.value = inString
        _dateDB.value = inMS
    }

    fun setTimeIn(time: String) {
        _timeIn.value = time
    }

    fun setTimeOut(time: String) {
        _timeOut.value = time
    }

    fun saveWork() {
        viewModelScope.launch {
            dao.insertWork(getAddWork())
        }
    }

    fun updateWork() {
        viewModelScope.launch {
            dao.updateWork(getUpdateWork())
        }
    }


    fun testOnClickInVM() {
        Log.i("WorkLog >", "Test on clicked !")
    }

}