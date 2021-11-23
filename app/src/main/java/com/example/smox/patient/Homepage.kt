package com.example.smox.patient

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.example.smox.CreateUsername
import com.example.smox.R
import com.example.smox.SplashActivity

class Homepage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.p_homepage)
        val data = intent.getStringExtra("name")
        //println(data)
        findViewById<TextView>(R.id.name).text = "HELLO, $data"
    }

    fun gotoSchedule(view: View) {
        val intent = Intent(this, Schedule::class.java)
        startActivity(intent)
        finish()
    }

    fun gotoDosage(view: View) {
        val intent = Intent(this, Dosage::class.java)
        startActivity(intent)
        finish()
    }
    fun gotoHistory(view: View) {
        val intent = Intent(this, History::class.java)
        startActivity(intent)
        finish()
    }

    fun gotoInformation(view: View) {
        val intent = Intent(this, Information::class.java)
        startActivity(intent)
        finish()
    }


}