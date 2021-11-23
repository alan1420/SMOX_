package com.example.smox.caretaker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.smox.R
import com.example.smox.patient.Dosage
import com.example.smox.patient.History
import com.example.smox.patient.Information
import com.example.smox.patient.Schedule

class Homepage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.c_homepage)
    }

    fun gotoSchedule(view: View) {
        val intent = Intent(this, com.example.smox.caretaker.Schedule::class.java)
        startActivity(intent)
        finish()
    }

    fun gotoDosage(view: View) {
        val intent = Intent(this, com.example.smox.caretaker.Dosage::class.java)
        startActivity(intent)
        finish()
    }
    fun gotoHistory(view: View) {
        val intent = Intent(this, com.example.smox.caretaker.History::class.java)
        startActivity(intent)
        finish()
    }

    fun gotoInformation(view: View) {
        val intent = Intent(this, com.example.smox.caretaker.Information::class.java)
        startActivity(intent)
        finish()
    }
}