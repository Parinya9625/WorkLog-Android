package com.parinya.worklog.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Work::class], version = 1, exportSchema = false)
abstract class WorkDatabase: RoomDatabase() {

    abstract fun workDao(): WorkDao

    companion object {
        @Volatile
        private var _instance: WorkDatabase? = null

        fun getInstance(context: Context): WorkDatabase {
            synchronized(this) {
                var instance = _instance
                if (instance == null) {
                    instance = Room.databaseBuilder(context.applicationContext, WorkDatabase::class.java, "works_db").build()
                }
                return instance
            }
        }
    }

}