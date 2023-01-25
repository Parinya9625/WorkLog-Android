package com.parinya.worklog.util

import android.os.Build
import androidx.annotation.RequiresApi
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
    }
}