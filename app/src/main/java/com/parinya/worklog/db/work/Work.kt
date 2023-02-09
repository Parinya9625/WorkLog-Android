package com.parinya.worklog.db.work

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import kotlin.random.Random

@Parcelize
@Entity("works")
data class Work(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,
    val viewType: Int = Random.nextInt(0, 4 + 1),
//    val viewType: Int = 0,
    @ColumnInfo(name = "date")
    val date: Long = 0L,
    @ColumnInfo(name = "timeIn")
    val timeIn: String = "",
    @ColumnInfo(name = "timeOut")
    val timeOut: String = "",
    @ColumnInfo(name = "activity")
    val activity: String = "",
    @ColumnInfo(name = "knowledge")
    val knowledge: String = "",
    @ColumnInfo(name = "problem")
    val problem: String = ""
) : Parcelable
