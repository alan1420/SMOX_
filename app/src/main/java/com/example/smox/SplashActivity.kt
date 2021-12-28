package com.example.smox

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Handler
import android.util.Log
import android.view.View
import com.android.volley.VolleyError
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GetTokenResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import org.json.JSONObject

class SplashActivity : AppCompatActivity() {

    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val firebaseUser = firebaseAuth.currentUser
        updateUI(firebaseUser)
    }

    // [START declare_auth]
    private lateinit var auth: FirebaseAuth
    private var fcm: FirebaseMessaging = FirebaseMessaging.getInstance()
    // [END declare_auth]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.launchpage)

        auth = Firebase.auth

        if (!isFilePresent(this, "storage.json")) {
            createFile(this,
                "storage.json", "{}")
        }
        if (!isFilePresent(this, "store_token.json")) {
            createFile(this,
                "store_token.json", "{}")
        }
        if (!isFilePresent(this, "store_token_fcm.json")) {
            createFile(this,
                "store_token_fcm.json", "{}")
        }
    }

    // [START on_start_check_user]
    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        auth.addAuthStateListener(this.authStateListener)
    }
    // [END on_start_check_user]

    private fun updateUI(user: FirebaseUser?) {
        val handler = Handler()
        handler.postDelayed({
            if (user != null) {
                val name = user.displayName.toString()

                getToken(this, object: TokenResult {
                    override fun onSuccess(token: String) {
                        val newData = JSONObject()
                        while(true) {
                            var data = readFile(this@SplashActivity, "store_token_fcm.json")
                            var jsonData = Gson().fromJson(data, JsonObject::class.java)
                            if (jsonData.has("sync")) {
                                newData.put("fcm_token", jsonData.get("fcm_token").asString)
                                break
                            }
                        }
                        println(newData)
                        sendDataPOST("signinCheck", token, newData, this@SplashActivity,
                            object : VolleyResult {
                                override fun onSuccess(response: JSONObject) {
                                    if (!response.has("error")) {
                                        val isRegister = response.getBoolean("is_registered")
                                        var isCompleted : Boolean? = null
                                        if (response.has("is_completed"))
                                            isCompleted = response.getBoolean("is_completed")
                                        if (isRegister)
                                            if (isCompleted == true) {
                                                //Toast.makeText(this@SplashActivity, "Data lengkap", Toast.LENGTH_SHORT).show()
                                                toHomePage(this@SplashActivity, response.getString("role").toInt(), response.getString("last_name"))
                                            }
                                            else
                                                roleSelect()
                                    } else {
                                        Toast.makeText(this@SplashActivity, "Please fill the data!", Toast.LENGTH_SHORT).show()
                                        val intent = Intent(this@SplashActivity, SignUp::class.java)
                                        intent.putExtra("fullname", name)
                                        intent.putExtra("uuid", user.uid)
                                        startActivity(intent)
                                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                    }
                                }

                                override fun onError(error: VolleyError?) {
                                    if (error != null) {
                                        Log.d("Volley", getVolleyError(error))
                                        Toast.makeText(this@SplashActivity,
                                            getVolleyError(error),
                                            Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        )

                    }
                })

                Log.d(TAG, "signInWithCredential:success")
            } else {
                signInPage()
            }
        }, 3000)
    }

    private fun signInPage() {
        //Toast.makeText(this, "Silahkan login terlebih dahulu", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, SignIn::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    fun roleSelect() {
        Toast.makeText(this, "Data belum lengkap", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, ConfirmRole::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    override fun onPause() {
        super.onPause()
        println("Ini pause")
    }

    override fun onStop() {
        super.onStop()
        println("Ini stop")
        auth.removeAuthStateListener(this.authStateListener)
    }

    companion object {
        private const val TAG = "LaunchPage"
    }
}