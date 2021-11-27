package com.example.smox

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.content.Intent
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.example.smox.patient.Homepage
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONObject

class test : AppCompatActivity() {
    private var data: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.p_username)
        data = intent.getIntExtra("role", 2)
    }

    fun register_p(view: View) {
        val intent = Intent(this, Homepage::class.java)
        startActivity(intent)
        finish()
    }
}

