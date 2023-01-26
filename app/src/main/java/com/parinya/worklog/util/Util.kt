package com.parinya.worklog.util

import android.os.Build
import android.text.InputType
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentManager
import com.google.android.material.textfield.TextInputLayout
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

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
                        val initDate = if (text.toString().isBlank()) {
                            InitDatePicker()
                        } else {
                            val formatter = DateTimeFormatter.ofPattern("dd/MM/yy")
                            val localDate = LocalDate.parse(text.toString(), formatter)

                            InitDatePicker(
                                localDate.dayOfMonth,
                                localDate.monthValue - 1,
                                localDate.year,
                            )
                        }

                        val datePicker = DatePicker(
                            initDatePicker = initDate,
                            onDateSet = {y, m, d ->
                                onDateSet(y, m, d)
                            }
                        )
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
                        val initTime = if (text.toString().isBlank()) {
                            InitTimePicker()
                        }  else {
                            val formatter = DateTimeFormatter.ofPattern("HH:mm")
                            val localTime = LocalTime.parse(text.toString(), formatter)

                            InitTimePicker(
                                hour = localTime.hour,
                                minute = localTime.minute,
                            )
                        }

                        val timePicker = TimePicker(
                            initTimePicker = initTime,
                            onTimeSet = {h, m ->
                                onTimeSet(h, m)
                            }
                        )

                        timePicker.show(fragmentManager, "TimePicker")
                    }
                }
            }
        }
    }
}