package com.parinya.worklog.db

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import kotlin.random.Random

@Parcelize
@Entity("works")
data class Work(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val viewType: Int = Random.nextInt(0, 4 + 1),
//    val viewType: Int = 0,
    val date: String = "",
    val timeIn: String = "",
    val timeOut: String = "",
    val activity: String = "",
    val knowledge: String = "",
    val problem: String = ""
) : Parcelable
