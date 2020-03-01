package com.shaun.mynote


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.GridView
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.Observer
import com.shaun.mynote.adapter.NoteGridViewAdapter
import com.shaun.mynote.db.Note
import com.shaun.mynote.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {

    private val gridView: GridView by lazy {
        this.findViewById<GridView>(R.id.grid_view)
    }
    private val addButton: ImageView by lazy {
        this.findViewById<ImageView>(R.id.button_add)
    }
    private lateinit var adapter: NoteGridViewAdapter
    private lateinit var viewModel: MainViewModel

    private var noteList: List<Note> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val view = TestBaseView(this)
        adapter = NoteGridViewAdapter(this)

        gridView.numColumns = 2
        gridView.adapter = adapter
        gridView.horizontalSpacing = 20
        gridView.verticalSpacing = 20
        gridView.onItemClickListener = gridOnItemClickListener
//        val encryptUtil = EncryptUtil(this)
//
//        val org = "我是笨蛋"
//        val encOrg = encryptUtil.encrypt(org)
//        Log.d("Test", "enc: $encOrg")
//
//        val dec = encryptUtil.decrypt(encOrg)
//        Log.d("Test", "dec: $dec")

        addButton.setOnClickListener {
            val intent = Intent(this, EditActivity::class.java)
            startActivity(intent)
        }
        setViewModel()
    }

    private fun setViewModel() {
        viewModel = MainViewModel(this)

        viewModel.getNoteList().observe(this, Observer {
            Log.d("Text", "xxx : $it")
            adapter.updateList(it)
            noteList = it
        })

        viewModel.getMessage().observe(this, Observer {
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        })

        viewModel.loadAllNote()
    }

    private val gridOnItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id -> run{
        Log.d("OnItemClickListener", "" + position + "")

        val note = noteList.getOrNull(position)
        if(note != null) {
            val intent = Intent(this, EditActivity::class.java)
            intent.putExtra(EditActivity.EXTRA_NOTE_ID, note.uid)
            startActivity(intent)
        }
    }}
}
