package com.example.smox.caretaker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smox.R


class Homepage : AppCompatActivity() {
    private var backToast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.c_homepage)

    }
    fun menu(view: View) {
        val intent = Intent(this, com.example.smox.caretaker.Menu::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    fun gotoSchedule(view: View) {
        val intent = Intent(this, com.example.smox.caretaker.Schedule::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    fun gotoDosage(view: View) {
        val intent = Intent(this, com.example.smox.caretaker.Dosage::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
    fun gotoHistory(view: View) {
        val intent = Intent(this, com.example.smox.caretaker.History::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    fun gotoInformation(view: View) {
        val intent = Intent(this, com.example.smox.caretaker.Patient::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}