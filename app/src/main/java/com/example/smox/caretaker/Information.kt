package com.example.smox.caretaker

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.smox.R
import com.example.smox.patient.Homepage

class Information : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.c_information)
    }

    fun backtoHome(view: View) {
        val intent = Intent(this, com.example.smox.caretaker.Homepage::class.java)
        startActivity(intent)
        finish()
    }
}