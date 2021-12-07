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

class Dosage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.p_dosage)

        var dataPatient = readFile(this, "storage.json")
        if (dataPatient != null) {
            val jsonData = Gson().fromJson(dataPatient, JsonObject::class.java)
            if (jsonData.has("medicine_list")) {
                val scheduleData = jsonData.get("medicine_list").asJsonArray
                for (i in 0 until scheduleData.size()) {
                    val item = scheduleData.get(i).asJsonObject
                    val slot = item.get("slot").asInt
                    if (slot == 1) {
                        findViewById<TextView>(R.id.name1).text = item.get("medicine_name").asString.toUpperCase()
                        findViewById<TextView>(R.id.instruction1).text = item.get("instruction").asString
                        findViewById<TextView>(R.id.dosage1).text = item.get("dosage").asString
                    } else if (slot == 2) {
                        findViewById<TextView>(R.id.name2).text = item.get("medicine_name").asString.toUpperCase()
                        findViewById<TextView>(R.id.instruction2).text = item.get("instruction").asString
                        findViewById<TextView>(R.id.dosage2).text = item.get("dosage").asString
                    }
                }
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