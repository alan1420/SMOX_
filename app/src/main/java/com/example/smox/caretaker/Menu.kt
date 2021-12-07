package com.example.smox.caretaker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import com.example.smox.R
import androidx.appcompat.app.AppCompatActivity
import com.example.smox.SplashActivity
import com.example.smox.createFile
import com.example.smox.readFile
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.c_menu.*
import com.google.gson.Gson
import com.google.gson.JsonObject

class Menu : AppCompatActivity() {

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.c_menu)

        var userData = readFile(this, "storage.json")
        if (userData != null) {
            val jsonData = Gson().fromJson(userData, JsonObject::class.java)
            val userData = jsonData.get("user_data").asJsonObject
            findViewById<TextView>(R.id.fullname).text = userData.get("fullname").getAsString()
            findViewById<TextView>(R.id.birthday).text = userData.get("birthday").getAsString()
            findViewById<TextView>(R.id.email).text = userData.get("email").getAsString()
            findViewById<TextView>(R.id.phoneNumber).text = userData.get("phoneNumber").getAsString()
            //println(userData)
        }
    }

    fun backtoHome(view: View) {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        val clickEffect = AnimationUtils.loadAnimation(this, R.anim.scale_up)
        view.startAnimation(clickEffect)
    }

    fun logout(view: View) {
        auth.signOut()
        createFile(this, "storage.json", "{}")
        val intent = Intent(this, SplashActivity::class.java)
        startActivity(intent)
        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        val clickEffect = AnimationUtils.loadAnimation(this, R.anim.scale_down)
        view.startAnimation(clickEffect)
    }
}