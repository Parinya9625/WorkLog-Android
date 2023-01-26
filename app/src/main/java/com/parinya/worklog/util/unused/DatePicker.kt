package com.parinya.worklog.util

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.*

class DatePicker(
    private val initDatePicker: InitDatePicker = InitDatePicker(),
    private val onDateSet: (year: Int, month: Int, day: Int) -> Unit = {y, m, d -> }
) : DialogFragment(), DatePickerDialog.OnDateSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        val year: Int = initDatePicker.year ?: calendar.get(Calendar.YEAR)
        val month: Int = initDatePicker.month ?: calendar.get(Calendar.MONTH)
        val day: Int = initDatePicker.day ?: calendar.get(Calendar.DAY_OF_MONTH)

        return DatePickerDialog(requireActivity(), this, year, month, day)
    }

    override fun onDateSet(datePicker: DatePicker?, year: Int, month: Int, day: Int) {
        onDateSet(year, month, day)
    }
}

data class InitDatePicker(
    val day: Int? = null,
    val month: Int? = null,
    val year: Int? = null,
)