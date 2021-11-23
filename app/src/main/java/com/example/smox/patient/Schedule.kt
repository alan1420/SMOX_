package com.example.smox.patient

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.smox.R

class Schedule : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.p_schedule)

    }

    fun backtoHome(view: View) {
        val intent = Intent(this, Homepage::class.java)
        startActivity(intent)
        finish()
    }
}