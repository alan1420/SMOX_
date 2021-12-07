package com.example.smox.patient

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.smox.R
import com.example.smox.readFile
import com.google.gson.Gson
import com.google.gson.JsonObject

class Information : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.p_caretaker)

        var dataPatient = readFile(this, "storage.json")
        if (dataPatient != null) {
            val jsonData = Gson().fromJson(dataPatient, JsonObject::class.java)
            if (jsonData.has("caretaker_data")) {
                val userData = jsonData.get("caretaker_data").asJsonObject
                findViewById<TextView>(R.id.caretakerName).text = userData.get("fullname").asString
                findViewById<TextView>(R.id.caretakerBirthday).text = userData.get("birthday").asString
                findViewById<TextView>(R.id.caretakerEmail).text = userData.get("email").asString
                findViewById<TextView>(R.id.caretakerPhone).text = userData.get("phoneNumber").asString
                //println(userData)
            }
        }
    }

    fun backtoHome(view: View) {
        val clickEffect = AnimationUtils.loadAnimation(this, R.anim.scale_up)
        view.startAnimation(clickEffect)
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}