package com.shaun.mynote

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.Observer
import com.shaun.mynote.db.Note
import com.shaun.mynote.util.AlertUtil
import com.shaun.mynote.viewmodel.EditNoteViewModel


class EditActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_NOTE_ID = "note_id"
    }

    private val backButton: CardView by lazy {
        this.findViewById<CardView>(R.id.button_back)
    }

    private val saveButton: CardView by lazy {
        this.findViewById<CardView>(R.id.button_save)
    }

    private val deleteButton: CardView by lazy {
        this.findViewById<CardView>(R.id.button_delete)
    }

    private val recoverButton: CardView by lazy {
        this.findViewById<CardView>(R.id.button_recover)
    }


    private val contentEdit: EditText by lazy {
        this.findViewById<EditText>(R.id.edit_content)
    }

    private val titleEdit: EditText by lazy {
        this.findViewById<EditText>(R.id.edit_title)
    }

    private lateinit var viewModel: EditNoteViewModel
    private var noteId:Int? = null
    private var savedNote: Note? = null

    private var alert: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

         noteId = if(intent.hasExtra(EXTRA_NOTE_ID)) {
            val id = intent.getIntExtra(EXTRA_NOTE_ID, -1)
            if(id != -1) {
                id
            } else {
                null
            }
        } else {
            null
        }

        viewModel = EditNoteViewModel(this)

        saveButton.setOnClickListener {
            val title = titleEdit.text.toString()
            val content = contentEdit.text.toString()
            alert = AlertUtil.showProgressBar(this, R.string.text_loading_save)
            alert?.show()
            if(noteId != null && savedNote != null) {
                val newNote = savedNote!!.copy(
                    title = title,
                    content = content
                )
                viewModel.updateNote(newNote) {
                    alert?.dismiss()
                    alert = null
                    finish()
                }
            } else {
                viewModel.insertNote(title, content) {
                    alert?.dismiss()
                    alert = null
                    finish()
                }
            }
        }
        if(noteId != null) {
            deleteButton.visibility = View.VISIBLE
            recoverButton.visibility = View.VISIBLE

            deleteButton.setOnClickListener {
                if(savedNote != null) {
                    alert = AlertUtil.showProgressBar(this, R.string.text_loading_delete)
                    alert?.show()
                    viewModel.deleteNote(savedNote!!) {
                        alert?.dismiss()
                        alert = null
                        finish()
                    }
                }
            }
            recoverButton.setOnClickListener {
                if(savedNote != null) {
                    inputNoteToView(savedNote!!)
                    Toast.makeText(this, "還原內容", Toast.LENGTH_LONG).show()
                }
            }
        } else {
            deleteButton.visibility = View.GONE
            recoverButton.visibility = View.GONE
        }

        backButton.setOnClickListener {
            finish()
        }

        loadNote()

        viewModel.getMessage().observe(this, Observer {
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        })
    }

    private fun loadNote() {
        if(noteId == null) {
            return
        }

        viewModel.getNote().observe(this, Observer {
            this.savedNote = it
            inputNoteToView(it)
        })

        viewModel.loadNote(noteId!!)
    }

    private fun inputNoteToView(note: Note) {
        titleEdit.setText(note.title, TextView.BufferType.EDITABLE)
        contentEdit.setText(note.content, TextView.BufferType.EDITABLE)
    }

}
