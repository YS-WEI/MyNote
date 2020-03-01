package com.shaun.mynote.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.shaun.mynote.db.DatabaseKeys.Companion.DatabaseName
import com.shaun.mynote.db.DatabaseKeys.Companion.Version



@Database(entities = [Note::class], version = Version)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao

    companion object {

        @Volatile private var INSTANCE: AppDatabase? = null
        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context,
                AppDatabase::class.java, DatabaseName)
                .build()

    }




}