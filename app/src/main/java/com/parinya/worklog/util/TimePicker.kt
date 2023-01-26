package com.parinya.worklog.util

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.util.*

class TimePicker(
    private val initTimePicker: InitTimePicker = InitTimePicker(),
    private val onTimeSet: (hour: Int, minute: Int) -> Unit = {h, m ->},
): DialogFragment(), TimePickerDialog.OnTimeSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        val hour = initTimePicker.hour ?: calendar.get(Calendar.HOUR_OF_DAY)
        val minute = initTimePicker.minute ?: calendar.get(Calendar.MINUTE)

        return TimePickerDialog(requireContext(), this, hour, minute, true)
    }

    override fun onTimeSet(timePicker: TimePicker?, hour: Int, minute: Int) {
        onTimeSet(hour, minute)
    }
}

data class InitTimePicker(
    val hour: Int? = null,
    val minute: Int? = null,
)