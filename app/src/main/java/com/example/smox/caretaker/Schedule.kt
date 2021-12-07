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
import com.google.gson.JsonArray
import com.google.gson.JsonObject

class Schedule : AppCompatActivity(){

    var slot1Data: JsonObject = JsonObject()
    var slot2Data: JsonObject = JsonObject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.c_schedule)

        val dataPatient = readFile(this, "storage.json")
        if (dataPatient != null) {
            val jsonData = Gson().fromJson(dataPatient, JsonObject::class.java)
            if (jsonData.has("medicine_list")) {
                val scheduleData = jsonData.get("medicine_list").asJsonArray
                for (i in 0 until scheduleData.size()) {
                    val item = scheduleData.get(i).asJsonObject
                    val slot = item.get("slot").asInt
                    if (slot == 1) {
                        val period = item.get("period").asString + " " + item.get("period_type").asString.toUpperCase()
                        findViewById<TextView>(R.id.name1).text = item.get("medicine_name").asString.toUpperCase()
                        findViewById<TextView>(R.id.period1).text = period
                        findViewById<TextView>(R.id.interval1).text = item.get("interval").asString
                        slot1Data = item
                    } else if (slot == 2) {
                        val period = item.get("period").asString + " " + item.get("period_type").asString.toUpperCase()
                        findViewById<TextView>(R.id.name2).text = item.get("medicine_name").asString.toUpperCase()
                        findViewById<TextView>(R.id.period2).text = period
                        findViewById<TextView>(R.id.interval2).text = item.get("interval").asString
                        slot2Data = item
                    }
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

    fun gotoSetOne(view: View) {
        val intent = Intent(this, SetOne::class.java)
        if (slot1Data.has("slot")) {
            val bytes: ByteArray = slot1Data.toString().toByteArray()
            intent.putExtra("slot_data", bytes)
        }
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    fun gotoSetTwo(view: View) {
        val intent = Intent(this, SetTwo::class.java)
        if (slot2Data.has("slot")) {
            val bytes: ByteArray = slot2Data.toString().toByteArray()
            intent.putExtra("slot_data", bytes)
        }
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }
}