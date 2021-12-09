package com.example.smox.caretaker

import android.widget.Toast
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import com.example.smox.SplashActivity
import com.android.volley.VolleyError
import com.example.smox.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GetTokenResult
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONObject


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
        if (intent.hasExtra("name")) {
            val data = intent.getStringExtra("name")
            //println(data)
            findViewById<TextView>(R.id.sa).text = "HELLO, $data"
        }
    }

    override fun onStart() {
        super.onStart()
        auth.addAuthStateListener(this.authStateListener)
    }

    override fun onResume() {
        super.onResume()

        val dataUser = readFile(this, "storage.json")
        if (dataUser != null) {
            val jsonData = Gson().fromJson(dataUser, JsonObject::class.java)
            if (jsonData.has("user_data")) {
                val name = jsonData.getAsJsonObject("user_data").get("name").asString
                findViewById<TextView>(R.id.sa).text = "HELLO, $name"
            }
        }

        //Jika tidak ada update dari activity lain
        if (intent.hasExtra("name")) {
            getToken(object: TokenResult {
                override fun onSuccess(token: GetTokenResult) {
                    var urlPath = "get-caretaker-data"
                    var jsonLocalData = readFile(this@Homepage, "storage.json")
                    var jsonData = Gson().fromJson(jsonLocalData, JsonObject::class.java)

                    if (jsonData.has("patient_data")) {
                        if (jsonData.get("patient_data").isJsonObject) {
                            val pData = jsonData.get("patient_data").asJsonObject
                            urlPath = urlPath + "?patient_id=" + pData.get("id").asString
                        }
                    }

                    sendDataGET(urlPath, token.token.toString(),this@Homepage,
                        object : VolleyResult {
                            override fun onSuccess(response: JSONObject) {
                                println(urlPath)
                                val isFileCreated: Boolean = createFile(this@Homepage,
                                    "storage.json", response.toString())
                                //proceed with storing the first todo or show ui
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
        }
    }

    fun menu(view: View) {
        val intent = Intent(this, Menu::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        val clickEffect = AnimationUtils.loadAnimation(this, R.anim.scale_up)
        view.startAnimation(clickEffect)
    }

    fun gotoSchedule(view: View) {
        val intent = Intent(this, Schedule::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        val clickEffect = AnimationUtils.loadAnimation(this, R.anim.scale_down)
        view.startAnimation(clickEffect)
    }

    fun gotoDosage(view: View) {
        val intent = Intent(this, Dosage::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        val clickEffect = AnimationUtils.loadAnimation(this, R.anim.scale_down)
        view.startAnimation(clickEffect)
    }

    fun gotoHistory(view: View) {
        val intent = Intent(this, History::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        val clickEffect = AnimationUtils.loadAnimation(this, R.anim.scale_down)
        view.startAnimation(clickEffect)
    }

    fun gotoInformation(view: View) {
        val intent = Intent(this, Patient::class.java)
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