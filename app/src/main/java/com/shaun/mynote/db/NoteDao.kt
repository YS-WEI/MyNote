package com.siang.wei.mybookmark.db

import androidx.room.*
import com.shaun.mynote.db.Note
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
interface NoteDao {

    @Query("SELECT * FROM note ORDER BY updateDate DESC")
    fun getAll(): Flowable<List<Note>>

    @Insert
    fun insert(vararg note: Note): Completable

    @Delete
    fun delete(note: Note): Completable

    @Update
    fun update(vararg note: Note): Completable

}