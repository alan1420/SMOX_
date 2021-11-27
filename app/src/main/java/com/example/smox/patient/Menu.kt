package com.example.smox.patient

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.example.smox.R
import androidx.appcompat.app.AppCompatActivity
import com.example.smox.SplashActivity
import kotlinx.android.synthetic.main.c_menu.*

class Menu : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.p_menu)

    }

    fun backtoHome(view: View) {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    fun logout(view: View) {
        val intent = Intent(this, SplashActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }


}