package com.shaun.mynote.db

import android.content.Context
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

class NoteDatebaseRepository constructor(context: Context){

    private val noteDao: NoteDao
    init {
        val database = AppDatabase.getInstance(context)
        noteDao = database.noteDao()
    }

    fun getAllNote() : Flowable<List<Note>> {
        return noteDao.getAll()
    }

    fun findById(uid: Int) : Single<Note> {
        return noteDao.findById(uid)
    }

    fun insert(note: Note): Completable {
        return noteDao.insert(note)
    }

    fun delete(note: Note) : Completable {
        return noteDao.delete(note)
    }

    fun update(note: Note):Completable {
        return noteDao.update(note)
    }

}