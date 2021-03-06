package com.example.smox.patient

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.VolleyError
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import org.json.JSONObject
import com.example.smox.*

class CreateUsername : AppCompatActivity() {

    var auth = FirebaseAuth.getInstance()
    var currentUser: FirebaseUser? = null
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.p_username)

        sharedPref = getSharedPreferences("smox", MODE_PRIVATE)
    }

    override fun onStart() {
        super.onStart()
        currentUser = auth.currentUser
    }

    fun register_p(view: View) {
        val clickEffect = AnimationUtils.loadAnimation(this, R.anim.scale_down)
        view.startAnimation(clickEffect)
        val mUsername = findViewById<EditText>(R.id.enterusername).text.toString()
        if (textIsNotEmpty(mUsername)) {
            val dataJson = JSONObject()

            val fcmToken = sharedPref.getString("fcm_token", null)
            if (fcmToken != null) {
                dataJson.put("fcm_token", fcmToken)
            }

            currentUser?.getIdToken(true)?.addOnSuccessListener {
                Log.d("Token ID", it.token.toString())
                dataJson.put("role", 2)
                dataJson.put("username", mUsername)
                sendDataPOST("signup-finalize", it.token.toString(), dataJson, this.applicationContext,
                    object : VolleyResult {
                        override fun onSuccess(response: JSONObject) {
                            val intent = Intent(this@CreateUsername, Homepage::class.java)
                            intent.putExtra("name", response.getString("name"))
                            startActivity(intent)
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                            finish()
                        }

                        override fun onError(error: VolleyError?) {
                            if (error != null) {
                                Toast.makeText(this@CreateUsername,
                                    getVolleyError(error),
                                    Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                )
            }
        } else
            Toast.makeText(this, "Username cannot be empty!", Toast.LENGTH_SHORT).show()
    }
}