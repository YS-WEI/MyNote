package com.shaun.mynote.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note")
data class Note(
    @PrimaryKey(autoGenerate = true) var uid: Int? = null,
    @ColumnInfo(name = "title")  var title: String = "",
    @ColumnInfo(name = "content")  var content: String = "",
    @ColumnInfo(name = "createDate")  var createDate: String = "",
    @ColumnInfo(name = "updateDate")  var updateDate: String = ""
)