package com.example.smox.caretaker

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
        setContentView(R.layout.c_menu)

        val userData = readFile(this, "storage.json")
        if (userData != null) {
            val jsonData = Gson().fromJson(userData, JsonObject::class.java)
            val userData = jsonData.get("user_data").asJsonObject
            findViewById<TextView>(R.id.fullname).text = userData.get("fullname").getAsString()
            findViewById<TextView>(R.id.birthday).text = userData.get("birthday").getAsString()
            findViewById<TextView>(R.id.email).text = userData.get("email").getAsString()
            findViewById<TextView>(R.id.phoneNumber).text = userData.get("phoneNumber").getAsString()
        }
    }

    fun backtoHome(view: View) {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        val clickEffect = AnimationUtils.loadAnimation(this, R.anim.scale_up)
        view.startAnimation(clickEffect)
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