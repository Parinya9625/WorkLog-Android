package com.parinya.worklog.db

import androidx.room.TypeConverter


class WorkLogTypeConverter {

    @TypeConverter
    fun fromListString(value: List<String>?): String? {
        return value?.joinToString(
            prefix = "|",
            separator = "|",
            postfix = "|",
        )
    }

    @TypeConverter
    fun toListString(value: String?): List<String>? {
        return value?.split("|")?.filter { it.isNotBlank() }
    }

}