package com.example.smox.caretaker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smox.*
import com.example.smox.readFile
import com.google.gson.Gson
import com.google.gson.JsonObject

class Dosage : AppCompatActivity() {
    private var backToast: Toast? = null

    var slot1Data: JsonObject = JsonObject()
    var slot1Index: Int = 99
    var slot2Data: JsonObject = JsonObject()
    var slot2Index: Int = 88

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.c_dosage)
    }

    override fun onResume() {
        super.onResume()
        slot1Data = JsonObject()
        slot2Data = JsonObject()
        slot1Index = 99
        slot2Index = 88
        findViewById<TextView>(R.id.name1).text = "-"
        findViewById<TextView>(R.id.instruction1).text = "-"
        findViewById<TextView>(R.id.dosage1).text = "-"
        findViewById<TextView>(R.id.name2).text = "-"
        findViewById<TextView>(R.id.instruction2).text = "-"
        findViewById<TextView>(R.id.dosage2).text = "-"

        var dataPatient = readFile(this, "storage.json")
        if (dataPatient != null) {
            val jsonData = Gson().fromJson(dataPatient, JsonObject::class.java)
            if (jsonData.has("medicine_list")) {
                val scheduleData = jsonData.get("medicine_list").asJsonArray
                for (i in 0 until scheduleData.size()) {
                    println(scheduleData)
                    val item = scheduleData.get(i).asJsonObject
                    val slot = item.get("slot").asInt
                    if (slot == 1) {
                        findViewById<TextView>(R.id.name1).text = item.get("medicine_name").asString.toUpperCase()
                        findViewById<TextView>(R.id.instruction1).text = item.get("instruction").asString
                        findViewById<TextView>(R.id.dosage1).text = item.get("dosage").asString
                        slot1Data = item
                        slot1Index = i
                    } else if (slot == 2) {
                        findViewById<TextView>(R.id.name2).text = item.get("medicine_name").asString.toUpperCase()
                        findViewById<TextView>(R.id.instruction2).text = item.get("instruction").asString
                        findViewById<TextView>(R.id.dosage2).text = item.get("dosage").asString
                        slot2Data = item
                        slot2Index = i
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
        val intent = Intent(this, Set::class.java)
        if (slot1Data.has("slot") && slot1Index != 99) {
            val bytes: ByteArray = slot1Data.toString().toByteArray()
            intent.putExtra("slot_data", bytes)
            intent.putExtra("index", slot1Index)
        }
        intent.putExtra("slot", 1)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    fun gotoSetTwo(view: View) {
        val intent = Intent(this, Set::class.java)
        if (slot2Data.has("slot")) {
            val bytes: ByteArray = slot2Data.toString().toByteArray()
            intent.putExtra("slot_data", bytes)
            intent.putExtra("index", slot2Index)
        }
        intent.putExtra("slot", 2)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }
}