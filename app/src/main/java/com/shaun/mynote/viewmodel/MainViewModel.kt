package com.shaun.mynote.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shaun.mynote.db.Note
import com.shaun.mynote.db.NoteDatebaseRepository
import com.shaun.mynote.util.EncryptUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(context: Context) : ViewModel() {
    private var encryptUtil: EncryptUtil = EncryptUtil(context)
    private var compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val repository: NoteDatebaseRepository = NoteDatebaseRepository(context)
    private val notesLiveDate : MutableLiveData<List<Note>> = MutableLiveData()

    private val messageLiveDate : MutableLiveData<String> = MutableLiveData()
    fun loadAllNote() {
        CoroutineScope(Dispatchers.IO).launch {
            val disposable = repository.getAllNote()
                .subscribeOn(Schedulers.io()).map {
                    val list = it.map { note ->
                        note.copy(title = encryptUtil.decrypt(note.title) ?: "",
                            content = encryptUtil.decrypt(note.content)?: "")
                    }
                    list
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    notesLiveDate.value = it
                }, {
                    //                Log.d("Error", "loadAllNote:", it)
                    messageLiveDate.value = "讀取資料失敗"
                })

            compositeDisposable.add(disposable)
        }
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