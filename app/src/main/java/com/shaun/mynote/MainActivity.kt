package com.shaun.mynote

import EncryptUtil
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val encryptUtil = EncryptUtil(this)

        val org = "我是笨蛋"
        val encOrg = encryptUtil.encrypt(org)
        Log.d("Test", "enc: $encOrg")

        val dec = encryptUtil.decrypt(encOrg)
        Log.d("Test", "dec: $dec")
    }
}
