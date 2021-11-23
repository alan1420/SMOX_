package com.example.smox.caretaker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.smox.R
import com.example.smox.patient.Homepage

class Schedule : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.c_schedule)

    }

    fun backtoHome(view: View) {
        val intent = Intent(this, com.example.smox.caretaker.Homepage::class.java)
        startActivity(intent)
        finish()
    }

    fun gotoSetOne(view: View) {
        val intent = Intent(this, SetOne::class.java)
        startActivity(intent)
        finish()
    }

    fun gotoSetTwo(view: View) {
        val intent = Intent(this, SetTwo::class.java)
        startActivity(intent)
        finish()
    }
}