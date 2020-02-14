package com.siang.wei.mybookmark

import android.content.Context
import com.shaun.mynote.db.Note
import com.siang.wei.mybookmark.db.AppDatabase
import com.siang.wei.mybookmark.db.NoteDao
import io.reactivex.Completable
import io.reactivex.Flowable

class NoteDatebaseRepository constructor(context: Context){

    private val noteDao: NoteDao
    init {
        val database = AppDatabase.getInstance(context)
        noteDao = database.noteDao()
    }

    fun getAllNote() : Flowable<List<Note>> {
        return noteDao.getAll()
    }

    fun findById(uid: Int) : Note {
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