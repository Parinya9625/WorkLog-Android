package com.parinya.worklog.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface WorkDao {

    @Insert
    suspend fun insertWork(work: Work)

    @Update
    suspend fun updateWork(work: Work)

    @Delete
    suspend fun deleteWork(work: Work)

    @Query("SELECT * FROM works")
    fun getWorks() : LiveData<List<Work>>

    @Query("SELECT * FROM works WHERE id = :id")
    fun getWork(id: Int): LiveData<Work>

    @Query("DELETE FROM works")
    suspend fun clearWorks()

}