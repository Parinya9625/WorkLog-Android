package com.parinya.worklog.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.parinya.worklog.db.note.Note
import com.parinya.worklog.db.note.NoteDao
import com.parinya.worklog.db.work.Work
import com.parinya.worklog.db.work.WorkDao

@Database(
    entities = [Work::class, Note::class],
    version = 2,
    exportSchema = false,
)
@TypeConverters(WorkLogTypeConverter::class)
abstract class WorkLogDatabase: RoomDatabase() {

    abstract fun workDao(): WorkDao
    abstract fun noteDao(): NoteDao

    companion object {
        @Volatile
        private var _instance: WorkLogDatabase? = null

        fun getInstance(context: Context): WorkLogDatabase {
            synchronized(this) {
                var instance = _instance
                if (instance == null) {
                    instance = Room.databaseBuilder(context.applicationContext, WorkLogDatabase::class.java, "worklog_db").build()
                }
                return instance
            }
        }
    }

}