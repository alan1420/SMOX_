package com.example.smox.patient

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.VolleyError
import com.example.smox.*
import kotlinx.android.synthetic.main.c_menu.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONObject

class Menu : AppCompatActivity() {

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
        }
    }

    fun backtoHome(view: View) {
        val clickEffect = AnimationUtils.loadAnimation(this, R.anim.scale_up)
        view.startAnimation(clickEffect)
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    fun logout(view: View) {
        getToken(this, object: TokenResult {
            override fun onSuccess(token: String) {
                sendDataGET("logout", token,this@Menu,
                    object : VolleyResult {
                        override fun onSuccess(response: JSONObject) {
                            getSharedPreferences("smox", MODE_PRIVATE).edit().remove("auth_token").remove("auth_expire").apply()
                            createFile(this@Menu,
                                "storage.json", "{}")
                            val clickEffect = AnimationUtils.loadAnimation(this@Menu, R.anim.scale_down)
                            view.startAnimation(clickEffect)
                            val intent = Intent(this@Menu, SplashActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
                            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                        }

                        override fun onError(error: VolleyError?) {
                            if (error != null) {
                                Log.d("Volley", getVolleyError(error))
                                Toast.makeText(this@Menu,
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