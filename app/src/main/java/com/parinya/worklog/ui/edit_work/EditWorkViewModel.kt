package com.parinya.worklog.ui.edit_work

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

class EditWorkViewModel(
    private val dao: WorkDao,
): ViewModel() {

    var _work: Work = Work()

    val _date = MutableLiveData<String>()
    val date: LiveData<String> = _date

    val _timeIn = MutableLiveData<String>()
    val timeIn: LiveData<String> = _timeIn

    val _timeOut = MutableLiveData<String>()
    val timeOut: LiveData<String> = _timeOut

    val _activity = MutableLiveData<String>()
    val activity: LiveData<String> = _activity

    val _knowledge = MutableLiveData<String>()
    val knowledge: LiveData<String> = _knowledge

    val _problem = MutableLiveData<String>()
    val problem: LiveData<String> = _problem

    fun setWork(work: Work) {
        _work = work

        _date.value = work.date
        _timeIn.value = work.timeIn
        _timeOut.value = work.timeOut
        _activity.value = work.activity
        _knowledge.value = work.knowledge
        _problem.value = work.problem

        Log.i("WorkLogRV", "${_problem.value} | ${problem.value}")
    }

    fun deleteWork() {
        viewModelScope.launch {
            dao.deleteWork(_work)
        }
    }

    fun updateWork() {
        viewModelScope.launch {
            dao.updateWork(getWork())
        }
    }

    private fun getWork(): Work {
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

}