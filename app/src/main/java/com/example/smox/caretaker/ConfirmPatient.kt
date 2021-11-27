package com.example.smox.caretaker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smox.R
import com.example.smox.SplashActivity

class ConfirmPatient : AppCompatActivity() {

    private var data: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.c_confirm_patient)
        data = intent.getIntExtra("role", 1)

    }

    fun confirm(view: View) {
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        val intent = Intent(this, Homepage::class.java)
        startActivity(intent)
        Toast.makeText(this, "Patient Added", Toast.LENGTH_SHORT).show()
    }
}