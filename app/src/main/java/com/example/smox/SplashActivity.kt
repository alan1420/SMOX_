package com.example.smox

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Handler
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.example.smox.patient.Homepage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.json.JSONObject

class SplashActivity : AppCompatActivity() {

    // [START declare_auth]
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    // [END declare_auth]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.launchpage)

        // [START initialize_auth]
        // Initialize Firebase Auth
        //auth = Firebase.auth
        // [END initialize_auth]
    }

    // [START on_start_check_user]
    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }
    // [END on_start_check_user]

    private fun updateUI(user: FirebaseUser?) {
        val handler = Handler()
        handler.postDelayed({
            if (user != null) {
                val nama = user.displayName.toString()
                Toast.makeText(this, "Welcome, $nama", Toast.LENGTH_SHORT).show()
                user.getIdToken(true).addOnSuccessListener {
                    loginCheck(it.token.toString())
                    Log.d("Token ID", it.token.toString()) // token #1
                }
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
        finish()
    }

    fun loginCheck(token: String) {
        val url = "http://192.168.0.88/smox/public/api/signinCheck"
        val data = JSONObject().put("token", token)
        println(data)

        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url, data,
            { response ->
                //Toast.makeText(this, response.toString(), Toast.LENGTH_SHORT).show()
                //println(response.toString())
                val isRegister = response.getBoolean("is_registered")
                var isCompleted : Boolean? = null
                if (response.has("is_completed"))
                    isCompleted = response.getBoolean("is_completed")

                if (isRegister)
                    // TODO: Redirect to homepage
                    if (isCompleted == true) {
                        Toast.makeText(this, "Data lengkap", Toast.LENGTH_SHORT).show()
                        toHomePage(response.getString("role").toInt(), response.getString("last_name"))
                    }
                    else
                        roleSelect()
                else
                    Toast.makeText(this, "Belum terdaftar", Toast.LENGTH_SHORT).show()
                //println("Status register: $data")
            },
            { error ->
            // TODO: Handle error
            }
        )
        val queue = VolleySingleton.getInstance(this.applicationContext).requestQueue
        queue.add(jsonObjectRequest)
    }

    fun roleSelect() {
        Toast.makeText(this, "Data belum lengkap", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, ConfirmRole::class.java)
        startActivity(intent)
        finish()
    }

    fun toHomePage(role: Int, name: String) {
        //To Patient
        if (role == 2) {
            val intent = Intent(this, Homepage::class.java)
            intent.putExtra("name", name)
            startActivity(intent)
            finish()
        }
        //To Caretaker
        else {
            val intent = Intent(this, com.example.smox.caretaker.Homepage::class.java)
            intent.putExtra("name", name)
            startActivity(intent)
            finish()
        }

    }

    companion object {
        private const val TAG = "LaunchPage"
    }
}