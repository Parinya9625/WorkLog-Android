package com.parinya.worklog.ui.manage_work

import android.icu.util.Calendar
import android.os.Build
import android.util.Log
import android.view.View
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
import java.util.*

@RequiresApi(Build.VERSION_CODES.N)
class ManageHomeViewModel(
    private val dao: WorkDao
): ViewModel() {

    var _work: Work = Work()

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

    fun setWork(work: Work) {
        _work = work

        _date.value = work.date
        _timeIn.value = work.timeIn
        _timeOut.value = work.timeOut
        _activity.value = work.activity
        _knowledge.value = work.knowledge
        _problem.value = work.problem
    }

    private fun getAddWork(): Work {
        return Work(
            date = date.value ?: "",
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
            date = date.value ?: "",
            timeIn = timeIn.value ?: "",
            timeOut = timeOut.value ?: "",
            activity = activity.value ?: "",
            knowledge = knowledge.value ?: "",
            problem = problem.value ?: "",
        )
    }

    fun setDate(date: String) {
        _date.value = date
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