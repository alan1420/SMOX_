package com.example.smox

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Handler
import android.util.Log
import com.android.volley.VolleyError
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GetTokenResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.json.JSONObject

class SplashActivity : AppCompatActivity() {

    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val firebaseUser = firebaseAuth.currentUser
        updateUI(firebaseUser)
    }

    // [START declare_auth]
    private lateinit var auth: FirebaseAuth
    // [END declare_auth]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.launchpage)

        auth = Firebase.auth

        val isFilePresent: Boolean = isFilePresent(this, "storage.json")
        if (!isFilePresent) {
            createFile(this,
                "storage.json", "{}")
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

                getToken(object: TokenResult {
                    override fun onSuccess(token: GetTokenResult) {
                        //TODO("Not yet implemented")
                        println(token.token)
                        println("Token expired at: " + token.expirationTimestamp)
                        sendDataGET("signinCheck", token.token.toString(), this@SplashActivity,
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