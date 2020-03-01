package com.shaun.mynote.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.shaun.mynote.R
import com.shaun.mynote.db.Note


class NoteGridViewAdapter(): BaseAdapter() {
    var mList: List<Note>? = null

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = LayoutInflater.from(parent!!.context).inflate(R.layout.adapter_note_item, parent, false)

        val titleText = view.findViewById<TextView>(R.id.text_title)
        val contentText = view.findViewById<TextView>(R.id.text_content)

        val note = mList?.get(position)
        if(note != null) {
            val title = note.title
            val content = note.content
            titleText.text = title
            contentText.text = content
        } else {
            titleText.text = ""
            contentText.text = ""
        }

        return view
    }

    override fun getItem(position: Int): Note? {
        if(mList != null) {
            return mList!![position]
        } else {
            return null
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        if(mList != null) {
            return mList!!.size
        } else {
            return 0
        }
    }

    fun updateList(list: List<Note>) {

        mList = list
        notifyDataSetChanged()

    }


}