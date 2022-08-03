package com.example.aidl

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Process
import android.os.Process.myPid
import android.widget.Button

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.button)
    }
}