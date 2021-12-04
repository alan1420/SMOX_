package com.example.smox.caretaker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import com.example.smox.R
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.c_menu.*

class ConnectPatient : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.c_connect)

    }

    fun connect(view: View) {
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        val intent = Intent(this, com.example.smox.caretaker.ConfirmPatient::class.java)
        startActivity(intent)
        val clickEffect = AnimationUtils.loadAnimation(this, R.anim.scale_down)
        view.startAnimation(clickEffect)
    }
}