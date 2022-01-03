package com.example.smox.caretaker

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

class Patient : AppCompatActivity() {
    var patient_name = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.c_patient)

        var pData = readFile(this, "storage.json")
        if (pData != null) {
            val jsonData = Gson().fromJson(pData, JsonObject::class.java)
            if (jsonData.has("patient_data")) {
                if (jsonData.get("patient_data").isJsonObject) {
                    val userData = jsonData.get("patient_data").asJsonObject
                    findViewById<TextView>(R.id.fullname).text = userData.get("fullname").getAsString()
                    findViewById<TextView>(R.id.birthday).text = userData.get("birthday").getAsString()
                    findViewById<TextView>(R.id.email).text = userData.get("email").getAsString()
                    findViewById<TextView>(R.id.phoneNumber).text = userData.get("phoneNumber").getAsString()
                    patient_name = userData.get("fullname").getAsString()
                }
            }
        }
    }

    fun backtoHome(view: View) {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        val clickEffect = AnimationUtils.loadAnimation(this, R.anim.scale_up)
        view.startAnimation(clickEffect)
    }

    fun changePatient(view: View) {
        val intent = Intent(this, PatientList::class.java)
        intent.putExtra("current_patient", patient_name)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        val clickEffect = AnimationUtils.loadAnimation(this, R.anim.scale_down)
        view.startAnimation(clickEffect)
    }

    fun addPatient(view: View) {
        val intent = Intent(this, ConnectPatient::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        val clickEffect = AnimationUtils.loadAnimation(this, R.anim.scale_down)
        view.startAnimation(clickEffect)
    }
}