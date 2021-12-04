package com.example.smox.caretaker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.smox.R

class Patient : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.c_patient)
    }

    fun backtoHome(view: View) {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        val clickEffect = AnimationUtils.loadAnimation(this, R.anim.scale_up)
        view.startAnimation(clickEffect)
    }

    fun changePatient(view: View) {
        val intent = Intent(this, com.example.smox.caretaker.PatientList::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        val clickEffect = AnimationUtils.loadAnimation(this, R.anim.scale_down)
        view.startAnimation(clickEffect)
    }

    fun addPatient(view: View) {
        val intent = Intent(this, com.example.smox.caretaker.ConnectPatient::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        val clickEffect = AnimationUtils.loadAnimation(this, R.anim.scale_down)
        view.startAnimation(clickEffect)
    }
}