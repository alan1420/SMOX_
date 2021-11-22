package com.example.smox

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class ConfirmRole : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.confirm)
    }

    fun pickPatient(view: View) {
        val intent = Intent(this, CreateUsername::class.java)
        intent.putExtra("role", 2)
        startActivity(intent)
        finish()
    }

    fun pickCaretaker(view: View) {
        val intent = Intent(this, SplashActivity::class.java)
        intent.putExtra("role", 1)
        startActivity(intent)
        finish()
    }
}