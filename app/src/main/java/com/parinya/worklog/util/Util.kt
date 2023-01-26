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
                        val formatter = DateTimeFormatter.ofPattern("dd/MM/yy")
                        val localDate = LocalDate.parse(text.toString(), formatter)

                        val datePicker = DatePicker(
                            initDatePicker = InitDatePicker(
                                localDate.dayOfMonth, localDate.monthValue - 1, localDate.year,
                            ),
                            onDateSet = {y, m, d ->
                                onDateSet(y, m, d)
                            }
                        )
                        datePicker.show(fragmentManager, "DatePicker")
                    }
                }
            }
        }
    }
}