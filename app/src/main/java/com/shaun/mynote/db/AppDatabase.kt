package com.siang.wei.mybookmark.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.shaun.mynote.db.Note
import com.siang.wei.mybookmark.db.DatabaseKeys.Companion.DatabaseName
import com.siang.wei.mybookmark.db.DatabaseKeys.Companion.Version


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
            Room.databaseBuilder(context.applicationContext,
                AppDatabase::class.java, DatabaseName)
                .build()

    }




}