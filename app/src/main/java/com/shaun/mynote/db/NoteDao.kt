package com.shaun.mynote.db

import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface NoteDao {

    @Query("SELECT * FROM note ORDER BY updateDate DESC")
    fun getAll(): Flowable<List<Note>>

    @Query("SELECT * FROM note WHERE uid LIKE :uid")
    fun findById(uid: Int): Single<Note>

    @Insert
    fun insert(vararg note: Note): Completable


    @Delete
    fun delete(note: Note): Completable

    @Update
    fun update(vararg note: Note): Completable

}