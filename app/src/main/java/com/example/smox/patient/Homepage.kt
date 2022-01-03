package com.example.smox.patient

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationUtils
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.android.volley.VolleyError
import kotlinx.android.synthetic.main.p_homepage.*
import com.example.smox.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class Homepage : AppCompatActivity() {

    var slot1Data = JsonObject()
    var slot2Data = JsonObject()
    var dataUser = JsonObject()

    lateinit var  receiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.p_homepage)
        if (intent.hasExtra("name")) {
            val data = intent.getStringExtra("name")
            findViewById<TextView>(R.id.sa).text = "HELLO, $data"
        }

        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val datafcm = intent?.getByteArrayExtra("fcm")
                val jsonData = Gson().fromJson(datafcm?.let { String(it) }, JsonObject::class.java)

                println(jsonData)

                AlertDialog
                    .Builder(this@Homepage)
                    .setMessage(jsonData.toString())
                    .setTitle("Ini adalah title")
                    .setPositiveButton("Ok") {_,_ ->}
                    .create().show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter("INTENT_ACTION_SEND_MESSAGE")
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter)

        //Jika tidak ada update dari activity lain
        if (intent.hasExtra("name")) {
            getToken(this, object: TokenResult {
                override fun onSuccess(token: String) {

                    sendDataGET("get-patient-data", token,this@Homepage,
                        object : VolleyResult {
                            override fun onSuccess(response: JSONObject) {
                                val isFileCreated: Boolean = createFile(this@Homepage,
                                    "storage.json", response.toString())
                                dataUser = Gson().fromJson(response.toString(), JsonObject::class.java)
                                loadData()
                                if (!isFileCreated) {
                                    //show error or try again.
                                    Toast.makeText(this@Homepage,
                                        "Error menyimpan data ke penyimpanan internal",
                                        Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onError(error: VolleyError?) {
                                if (error != null) {
                                    Log.d("Volley", getVolleyError(error))
                                    Toast.makeText(this@Homepage,
                                        getVolleyError(error),
                                        Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    )
                }
            })
        } else {
            val jsonData = readFile(this, "storage.json")
            if (jsonData != null) {
                dataUser = Gson().fromJson(jsonData, JsonObject::class.java)
                loadData()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
    }

    fun loadData() {
        slot1Data = JsonObject()
        slot2Data = JsonObject()
        var upcoming1 = 0
        var upcoming2 = 0
        var stateA = false
        var stateB = false

        if (dataUser.has("user_data")) {
            val name = dataUser.getAsJsonObject("user_data").get("name").asString
            findViewById<TextView>(R.id.sa).text = "HELLO, $name"
        }
        if (dataUser.has("medicine_list")) {
            dataUser.get("medicine_list").asJsonArray.forEach {
                val objData = it.asJsonObject
                val slot = objData.get("slot").asString
                if (slot == "1") {
                    slot1Data = objData
                } else if (slot == "2") {
                    slot2Data = objData
                }
            }
        }

        if (slot1Data.size() > 0) {
            val startDate = ZonedDateTime.parse((slot1Data.get("updated_at").asString)).toLocalDate()
            val startTime = LocalTime.parse(slot1Data.get("start_time").asString)
            val startDateTime = startDate.atTime(startTime)
            var upDt = startDateTime
            while (upDt.isBefore(LocalDateTime.now())) {
                upDt = upDt.plusHours(slot1Data.get("interval").asLong)
            }
            upcoming1 = upDt.toLocalTime().toSecondOfDay()
        }

        if (slot2Data.size() > 0) {
            val startDate = ZonedDateTime.parse((slot2Data.get("updated_at").asString)).toLocalDate()
            val startTime = LocalTime.parse(slot2Data.get("start_time").asString)
            val startDateTime = startDate.atTime(startTime)
            var upDt = startDateTime
            while (upDt.isBefore(LocalDateTime.now())) {
                upDt = upDt.plusHours(slot2Data.get("interval").asLong)
            }
            upcoming2 = upDt.toLocalTime().toSecondOfDay()
        }
        if(upcoming1 > 0) stateA = true
        if(upcoming2 > 0) stateB = true

        if(((upcoming1 < upcoming2) && (stateA && stateB)) || (stateA && !stateB)){
            findViewById<TextView>(R.id.hour).text = LocalTime.ofSecondOfDay(upcoming1.toLong()).format(
                DateTimeFormatter.ofPattern("hh")).toString()
            findViewById<TextView>(R.id.minute).text = LocalTime.ofSecondOfDay(upcoming1.toLong()).format(
                DateTimeFormatter.ofPattern("mm")).toString()
            findViewById<TextView>(R.id.ampm).text = LocalTime.ofSecondOfDay(upcoming1.toLong()).format(
                DateTimeFormatter.ofPattern("a")).toString()
            findViewById<TextView>(R.id.medicine).text = slot1Data.get("medicine_name").asString
            findViewById<TextView>(R.id.dosage).text = slot1Data.get("dosage").asString + " TABLET"
            findViewById<TextView>(R.id.instruction).text = slot1Data.get("instruction").asString
        }

        if(((upcoming2 < upcoming1) && (stateA && stateB)) || (stateB && !stateA)){
            findViewById<TextView>(R.id.hour).text = LocalTime.ofSecondOfDay(upcoming2.toLong()).format(
                DateTimeFormatter.ofPattern("hh")).toString()
            findViewById<TextView>(R.id.minute).text = LocalTime.ofSecondOfDay(upcoming2.toLong()).format(
                DateTimeFormatter.ofPattern("mm")).toString()
            findViewById<TextView>(R.id.ampm).text = LocalTime.ofSecondOfDay(upcoming2.toLong()).format(
                DateTimeFormatter.ofPattern("a")).toString()
            findViewById<TextView>(R.id.medicine).text = slot2Data.get("medicine_name").asString
            findViewById<TextView>(R.id.dosage).text = slot2Data.get("dosage").asString + " TABLET"
            findViewById<TextView>(R.id.instruction).text = slot2Data.get("instruction").asString
        }

        if (stateA || stateB) {
            findViewById<RelativeLayout>(R.id.data).visibility = View.VISIBLE
            findViewById<RelativeLayout>(R.id.nodata).visibility = View.INVISIBLE
        } else {
            findViewById<RelativeLayout>(R.id.nodata).visibility = View.VISIBLE
            findViewById<RelativeLayout>(R.id.data).visibility = View.INVISIBLE
        }
    }

    fun gotoSchedule(view: View) {
        val clickEffect = AnimationUtils.loadAnimation(this, R.anim.scale_down)
        view.startAnimation(clickEffect)
        val intent = Intent(this, Schedule::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    fun gotoDosage(view: View) {
        val clickEffect = AnimationUtils.loadAnimation(this, R.anim.scale_down)
        view.startAnimation(clickEffect)
        val intent = Intent(this, Dosage::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    fun gotoHistory(view: View) {
        val clickEffect = AnimationUtils.loadAnimation(this, R.anim.scale_down)
        view.startAnimation(clickEffect)
        val intent = Intent(this, History::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    fun gotoInformation(view: View) {
        val clickEffect = AnimationUtils.loadAnimation(this, R.anim.scale_down)
        view.startAnimation(clickEffect)
        val intent = Intent(this, Information::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    fun menu(view: View) {
        val clickEffect = AnimationUtils.loadAnimation(this, R.anim.scale_up)
        view.startAnimation(clickEffect)
        val intent = Intent(this, Menu::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}