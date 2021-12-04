package com.example.smox.caretaker

import android.widget.Toast
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import com.example.smox.R
import com.example.smox.SplashActivity
import com.google.firebase.auth.FirebaseAuth


class Homepage : AppCompatActivity() {
    private var backToast: Toast? = null

    private val auth = FirebaseAuth.getInstance()
    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null) {
            val intent = Intent(this, SplashActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.c_homepage)
        val data = intent.getStringExtra("name")
        //println(data)
        findViewById<TextView>(R.id.name).text = "HELLO, $data"

    }

    override fun onStart() {
        super.onStart()
        auth.addAuthStateListener(this.authStateListener)
    }

    fun menu(view: View) {
        val intent = Intent(this, com.example.smox.caretaker.Menu::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        val clickEffect = AnimationUtils.loadAnimation(this, R.anim.scale_up)
        view.startAnimation(clickEffect)
    }

    fun gotoSchedule(view: View) {
        val intent = Intent(this, com.example.smox.caretaker.Schedule::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        val clickEffect = AnimationUtils.loadAnimation(this, R.anim.scale_down)
        view.startAnimation(clickEffect)
    }

    fun gotoDosage(view: View) {
        val intent = Intent(this, com.example.smox.caretaker.Dosage::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        val clickEffect = AnimationUtils.loadAnimation(this, R.anim.scale_down)
        view.startAnimation(clickEffect)
    }
    fun gotoHistory(view: View) {
        val intent = Intent(this, com.example.smox.caretaker.History::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        val clickEffect = AnimationUtils.loadAnimation(this, R.anim.scale_down)
        view.startAnimation(clickEffect)
    }

    fun gotoInformation(view: View) {
        val intent = Intent(this, com.example.smox.caretaker.Patient::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        val clickEffect = AnimationUtils.loadAnimation(this, R.anim.scale_down)
        view.startAnimation(clickEffect)
    }

    override fun onStop() {
        super.onStop()
        auth.removeAuthStateListener(this.authStateListener)
    }
}