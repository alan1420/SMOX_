package com.example.smox.caretaker

import android.content.BroadcastReceiver
import android.content.Context
import android.widget.Toast
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.smox.SplashActivity
import com.android.volley.VolleyError
import com.example.smox.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GetTokenResult
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class Homepage : AppCompatActivity() {
    private var backToast: Toast? = null
    lateinit var  receiver: BroadcastReceiver

    var slot1Data = JsonObject()
    var slot2Data = JsonObject()
    var dataUser = JsonObject()

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

    override fun onStart() {
        super.onStart()
        auth.addAuthStateListener(this.authStateListener)
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter("INTENT_ACTION_SEND_MESSAGE")
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter)

        //Jika tidak ada update dari activity lain
        if (intent.hasExtra("name")) {
            getToken(this, object: TokenResult {
                override fun onSuccess(token: String) {
                    var urlPath = "get-caretaker-data"
                    var jsonLocalData = readFile(this@Homepage, "storage.json")
                    var jsonData = Gson().fromJson(jsonLocalData, JsonObject::class.java)

                    if (jsonData.has("patient_data")) {
                        if (jsonData.get("patient_data").isJsonObject) {
                            val pData = jsonData.get("patient_data").asJsonObject
                            urlPath = urlPath + "?patient_id=" + pData.get("id").asString
                        }
                    }

                    sendDataGET(urlPath, token,this@Homepage,
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

    fun menu(view: View) {
        val clickEffect = AnimationUtils.loadAnimation(this, R.anim.scale_up)
        view.startAnimation(clickEffect)
        val intent = Intent(this, Menu::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    fun gotoSchedule(view: View) {
        val intent = Intent(this, Schedule::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        val clickEffect = AnimationUtils.loadAnimation(this, R.anim.scale_down)
        view.startAnimation(clickEffect)
    }

    fun gotoDosage(view: View) {
        val intent = Intent(this, Dosage::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        val clickEffect = AnimationUtils.loadAnimation(this, R.anim.scale_down)
        view.startAnimation(clickEffect)
    }

    fun gotoHistory(view: View) {
        val intent = Intent(this, History::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        val clickEffect = AnimationUtils.loadAnimation(this, R.anim.scale_down)
        view.startAnimation(clickEffect)
    }

    fun gotoInformation(view: View) {
        val intent = Intent(this, Patient::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        val clickEffect = AnimationUtils.loadAnimation(this, R.anim.scale_down)
        view.startAnimation(clickEffect)
    }

    override fun onStop() {
        super.onStop()
        auth.removeAuthStateListener(this.authStateListener)
    }
}