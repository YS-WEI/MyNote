package com.shaun.mynote.viewmodel

import EncryptUtil
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shaun.mynote.db.Note
import com.siang.wei.mybookmark.NoteDatebaseRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.*

class EditNoteViewModel(context: Context) : ViewModel() {

    val encryptUtil = EncryptUtil(context)
    private var compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val repository: NoteDatebaseRepository = NoteDatebaseRepository(context)
    private val noteLiveDate : MutableLiveData<Note> = MutableLiveData()

    private val messageLiveDate : MutableLiveData<String> = MutableLiveData()
    fun loadNote(uid: Int) {
        val note = repository.findById(uid)

        noteLiveDate.value = note.copy(title = encryptUtil.decrypt(note.title) ?: "",
            content = encryptUtil.decrypt(note.content) ?: "")
    }

    fun insertNote(title : String, content: String) {

        val calendar = Calendar.getInstance()
        val newNote = Note(
                title = encryptUtil.encrypt(title),
                content = encryptUtil.encrypt(content),
                createDate = calendar.time.toString(),
                updateDate = calendar.time.toString()
            )

        val disposable = repository.insert(newNote)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ messageLiveDate.value = "新增成功" }, { messageLiveDate.value = "新增失敗" })

        compositeDisposable.add(disposable)
    }

    fun updateNote(note : Note) {
        val calendar = Calendar.getInstance()
        val encNote = note.copy(
            title = encryptUtil.encrypt(note.title),
            content = encryptUtil.encrypt(note.content),
            updateDate = calendar.time.toString()
        )

        val disposable = repository.update(encNote)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ messageLiveDate.value = "儲存成功" }, { messageLiveDate.value = "儲存失敗" })

        compositeDisposable.add(disposable)
    }

    fun deleteNote(note : Note) {
        val disposable = repository.delete(note)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ messageLiveDate.value = "刪除成功" }, { messageLiveDate.value = "刪除失敗" })


        compositeDisposable.add(disposable)
    }

    fun getNote(): LiveData<Note> {
        return noteLiveDate
    }

    fun getMessage() : LiveData<String> {
        return messageLiveDate
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}