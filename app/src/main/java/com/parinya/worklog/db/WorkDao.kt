package com.parinya.worklog.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.parinya.worklog.FilterSortedBy

@Dao
interface WorkDao {

    @Insert
    suspend fun insertWork(work: Work)

    @Update
    suspend fun updateWork(work: Work)

    @Delete
    suspend fun deleteWork(work: Work)

    @Query(
        "SELECT * FROM works " +
            "WHERE " +
                "CASE WHEN :sortedBy = 'Uncompleted' " +
                    "THEN (" +
                        "date = 0 OR " +
                        "timeIn = '' OR " +
                        "timeOut = '' OR " +
                        "activity = '' OR " +
                        "knowledge = '' " +
                    ") ELSE id NOT NULL END AND " +
                "CASE WHEN :dateRangeFrom > 0 " +
                    "THEN date >= :dateRangeFrom ELSE date >= (SELECT MIN(date) FROM works) END AND " +
                "CASE WHEN :dateRangeTo > 0 " +
                    "THEN date <= :dateRangeTo ELSE date <= (SELECT MAX(date) FROM works) END " +
            "ORDER BY " +
                "CASE WHEN :sortedBy = 'None' OR :sortedBy = 'Uncompleted' THEN id END ASC," +
                "CASE WHEN :sortedBy = 'DateAsc' THEN date END ASC," +
                "CASE WHEN :sortedBy = 'DateDes' THEN date END DESC "
    )
    fun getWorks(sortedBy: FilterSortedBy, dateRangeFrom: Long = 0, dateRangeTo: Long = 0) : LiveData<List<Work>>

    @Query("SELECT * FROM works WHERE id = :id")
    fun getWork(id: Int): LiveData<Work>

    @Query("DELETE FROM works")
    suspend fun clearWorks()

}