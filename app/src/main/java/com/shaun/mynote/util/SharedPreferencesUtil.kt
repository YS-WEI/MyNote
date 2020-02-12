package com.shaun.mynote.util

import android.content.Context


object SharedPreferencesUtil {

    private val PREF_NAME = "MY_NOTE_PREF"
    private val PREF_IV_KEY = "PREF_IV_KEY"
    private val PREF_AES_KEY = "PREF_AES_KEY"

    fun setIV(context: Context, iv: String) {
        val preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        preferences.edit().putString(PREF_IV_KEY, iv).commit()
    }

    fun getIV(context: Context): String? {
        val preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return preferences.getString(PREF_IV_KEY, "")
    }

    fun setAESKey(context: Context, key: String) {
        val preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        preferences.edit().putString(PREF_AES_KEY, key).commit()
    }

    fun getAESKey(context: Context): String? {
        val preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return preferences.getString(PREF_AES_KEY, "")
    }
}