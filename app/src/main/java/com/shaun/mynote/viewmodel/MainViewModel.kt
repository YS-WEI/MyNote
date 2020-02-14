package com.shaun.mynote.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shaun.mynote.db.Note
import com.siang.wei.mybookmark.NoteDatebaseRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MainViewModel(context: Context) : ViewModel() {

    private var compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val repository: NoteDatebaseRepository = NoteDatebaseRepository(context)
    private val notesLiveDate : MutableLiveData<List<Note>> = MutableLiveData()

    private val messageLiveDate : MutableLiveData<String> = MutableLiveData()
    fun loadAllNote() {

        val disposable = repository.getAllNote()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                notesLiveDate.value = it
            },{
//                Log.d("Error", "loadAllNote:", it)
                messageLiveDate.value = "讀取資料失敗"
            })

        compositeDisposable.add(disposable)

    }

    fun insertNote(note : Note) {
        val disposable = repository.insert(note)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ messageLiveDate.value = "新增成功" }, { messageLiveDate.value = "新增失敗" })

        compositeDisposable.add(disposable)
    }

    fun updateNote(note : Note) {

        val disposable = repository.update(note)
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

    fun getNoteList(): LiveData<List<Note>> {
        return notesLiveDate
    }

    fun getMessage() : LiveData<String> {
        return messageLiveDate
    }


    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}