package com.parinya.worklog.db.note

import android.graphics.Color
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity("notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id")
    val id: Int = 0,

    val title: String = "",
    val text: String = "",
    val color: Int = Color.WHITE,
    val tags: List<String> = listOf(),
    val isPinned: Boolean = false,
) : Parcelable