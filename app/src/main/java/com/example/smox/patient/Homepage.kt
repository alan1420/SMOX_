package com.example.smox.patient

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import com.example.smox.R

class Homepage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.p_homepage)
        val data = intent.getStringExtra("name")
        //println(data)
        findViewById<TextView>(R.id.name).text = "HELLO, $data"
    }
}