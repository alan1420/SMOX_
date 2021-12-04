package com.example.smox.caretaker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import com.example.smox.R
import androidx.appcompat.app.AppCompatActivity
import com.example.smox.SplashActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.c_menu.*

class Menu : AppCompatActivity() {

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.c_menu)

    }

    fun backtoHome(view: View) {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        val clickEffect = AnimationUtils.loadAnimation(this, R.anim.scale_up)
        view.startAnimation(clickEffect)
    }

    fun logout(view: View) {
        auth.signOut()
        val intent = Intent(this, SplashActivity::class.java)
        startActivity(intent)
        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        val clickEffect = AnimationUtils.loadAnimation(this, R.anim.scale_down)
        view.startAnimation(clickEffect)
    }
}