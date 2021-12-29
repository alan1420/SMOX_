package com.example.smox.patient

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import com.example.smox.R
import androidx.appcompat.app.AppCompatActivity
import com.example.smox.SplashActivity
import com.example.smox.createFile
import com.example.smox.readFile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.c_menu.*

class Menu : AppCompatActivity() {

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var fcm: FirebaseMessaging = FirebaseMessaging.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.p_menu)

        var dataPatient = readFile(this, "storage.json")
        if (dataPatient != null) {
            val jsonData = Gson().fromJson(dataPatient, JsonObject::class.java)
            val userData = jsonData.get("user_data").asJsonObject
            findViewById<TextView>(R.id.fullname).text = userData.get("fullname").getAsString()
            findViewById<TextView>(R.id.username).text = "username: " + userData.get("username").getAsString()
            findViewById<TextView>(R.id.birthday).text = userData.get("birthday").getAsString()
            findViewById<TextView>(R.id.email).text = userData.get("email").getAsString()
            findViewById<TextView>(R.id.phoneNumber).text = userData.get("phoneNumber").getAsString()
            //println(userData)
        }
    }

    fun backtoHome(view: View) {
        val clickEffect = AnimationUtils.loadAnimation(this, R.anim.scale_up)
        view.startAnimation(clickEffect)
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    fun logout(view: View) {
        auth.signOut()
        fcm.deleteToken()
        createFile(this,
            "storage.json", "{}")
        createFile(this,
            "store_token.json", "{}")
        createFile(this,
            "store_token_fcm.json", "{}")
        val clickEffect = AnimationUtils.loadAnimation(this, R.anim.scale_down)
        view.startAnimation(clickEffect)
        val intent = Intent(this, SplashActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}