package com.parinya.worklog.util

import android.os.Build
import android.text.InputType
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

class Util {

    companion object {
        @RequiresApi(Build.VERSION_CODES.O)
        fun epochSecToDateString(sec: Long) : String {
            val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
            val date = LocalDateTime.ofEpochSecond(sec, 0, ZoneOffset.UTC)
            return formatter.format(date)
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun convertInputToDatePicker(
            fragmentManager: FragmentManager,
            textInputLayout: TextInputLayout,
            onDateSet: (year: Int, month: Int, day: Int) -> Unit = {y, m, d -> }
        ) {
            if (textInputLayout.editText != null) {
                val editText = textInputLayout.editText as EditText

                editText.apply {
                    isClickable = true
                    isFocusable = false
                    inputType = InputType.TYPE_NULL
                    setOnClickListener {
                        val calendar = Calendar.getInstance()
                        val datePicker = MaterialDatePicker.Builder.datePicker()
                            .setSelection(
                                if (text.toString().isNotBlank()) {
                                    val formatter = DateTimeFormatter.ofPattern("dd/MM/yy")
                                    val localDate = LocalDate.parse(text.toString(), formatter)
                                    calendar.set(Calendar.DAY_OF_MONTH, localDate.dayOfMonth)
                                    calendar.set(Calendar.MONTH, localDate.monthValue - 1)
                                    calendar.set(Calendar.YEAR, localDate.year)

                                    calendar.timeInMillis
                                } else {
                                    MaterialDatePicker.todayInUtcMilliseconds()
                                }
                            )
                            .build()

                        datePicker.addOnPositiveButtonClickListener {
                            calendar.timeInMillis = it

                            onDateSet(
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH),
                            )
                        }
                        datePicker.show(fragmentManager, "DatePicker")
                    }
                }
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun convertInputToTimePicker(
            fragmentManager: FragmentManager,
            textInputLayout: TextInputLayout,
            onTimeSet: (hour: Int, minute: Int) -> Unit = {h, m ->}
        ) {
            if (textInputLayout.editText != null) {
                val editText = textInputLayout.editText as EditText

                editText.apply {
                    isClickable = true
                    isFocusable = false
                    inputType = InputType.TYPE_NULL
                    setOnClickListener {
                        val timePicker = MaterialTimePicker.Builder()

                        if (text.toString().isNotBlank()) {
                            val formatter = DateTimeFormatter.ofPattern("HH:mm")
                            val localTime = LocalTime.parse(text.toString(), formatter)

                            timePicker
                                .setHour(localTime.hour)
                                .setMinute(localTime.minute)
                        }

                        timePicker.setTimeFormat(TimeFormat.CLOCK_24H)

                        val timePickerBuild = timePicker.build()
                        timePickerBuild.addOnPositiveButtonClickListener {
                            onTimeSet(
                                timePickerBuild.hour,
                                timePickerBuild.minute,
                            )
                        }

                        timePickerBuild.show(fragmentManager, "TimePicker")
                    }
                }
            }
        }
    }
}