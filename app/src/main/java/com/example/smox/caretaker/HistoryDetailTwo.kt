package com.example.smox.caretaker

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.smox.R
import com.example.smox.patient.History

class HistoryDetailTwo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.history_detail)
    }

    fun back(view: View) {
        val intent = Intent(this, com.example.smox.caretaker.History::class.java)
        startActivity(intent)
        finish()
    }
}