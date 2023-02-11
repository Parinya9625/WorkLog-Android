package com.parinya.worklog.util

import android.os.Build
import android.text.InputType
import android.widget.EditText
import androidx.annotation.MenuRes
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
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
        fun dateToString(ms: Long, pattern: String = "dd MMMM y"): String {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = ms
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val month = calendar.get(Calendar.MONTH)
            val year = calendar.get(Calendar.YEAR)

            val date = String.format("%02d/%02d/%d", day, month + 1, year)
            val formatter = DateTimeFormatter.ofPattern("dd/MM/y")
            val localDate = LocalDate.parse(date, formatter)

            val formatterV2 = DateTimeFormatter.ofPattern(pattern)
            return localDate.format(formatterV2)
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun localDateToCalendar(localDate: LocalDate): Calendar {
            val calendar = Calendar.getInstance()

            calendar.set(Calendar.DAY_OF_MONTH, localDate.dayOfMonth)
            calendar.set(Calendar.MONTH, localDate.monthValue - 1)
            calendar.set(Calendar.YEAR, localDate.year)

            return calendar
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun convertInputToDatePicker(
            fragmentManager: FragmentManager,
            textInputLayout: TextInputLayout,
            onDateSet: (calendar: Calendar) -> Unit = {},
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
                                    val formatter = DateTimeFormatter.ofPattern("dd MMMM y")
                                    val localDate = LocalDate.parse(text.toString(), formatter)

                                    localDateToCalendar(localDate).timeInMillis
                                } else {
                                    MaterialDatePicker.todayInUtcMilliseconds()
                                }
                            )
                            .build()

                        datePicker.addOnPositiveButtonClickListener {
                            calendar.timeInMillis = it

                            onDateSet(calendar)
                        }
                        datePicker.show(fragmentManager, "DatePicker")
                    }
                }
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun convertInputToDateRangePicker(
            fragmentManager: FragmentManager,
            textInputLayout: TextInputLayout,
            onDateRangeSet: (from: Calendar, to: Calendar) -> Unit = {from, to ->},
            splitChar: String = "➜",
        ) {
            if (textInputLayout.editText != null) {
                val editText = textInputLayout.editText as EditText

                editText.apply {
                    isClickable = true
                    isFocusable = false
                    inputType = InputType.TYPE_NULL
                    setOnClickListener {
                        val datePickerBuilder = MaterialDatePicker.Builder.dateRangePicker()
                        try {
                            if (editText.text.isNotBlank()) {
                                val dates = editText.text.split(splitChar)
                                val formatter = DateTimeFormatter.ofPattern("dd MMM y")

                                val fromDate = LocalDate.parse(dates[0].trim(), formatter)
                                val toDate = LocalDate.parse(dates[1].trim(), formatter)

                                datePickerBuilder.setSelection(
                                    Pair(
                                        localDateToCalendar(fromDate).timeInMillis,
                                        localDateToCalendar(toDate).timeInMillis
                                    )
                                )
                            }
                        } catch (_: Exception) { }

                        val datePicker = datePickerBuilder.build()
                        datePicker.addOnPositiveButtonClickListener {
                            val fromCalandar = Calendar.getInstance()
                            val toCalandar = Calendar.getInstance()

                            fromCalandar.timeInMillis = it.first
                            toCalandar.timeInMillis = it.second

                            onDateRangeSet(fromCalandar, toCalandar)
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
                        timePicker.setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)

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

        fun setupToolbar(fragment: Fragment, toolbar: Toolbar, isTopLevel: Boolean = false, @MenuRes menuId: Int? = null) {
            val navController = fragment.findNavController()
            toolbar.setupWithNavController(navController, AppBarConfiguration(
                topLevelDestinationIds = if (isTopLevel && navController.currentDestination != null)
                    setOf(navController.currentDestination!!.id) else setOf()
            ))
            if (menuId != null) {
                toolbar.inflateMenu(menuId)
            }
        }

    }
}