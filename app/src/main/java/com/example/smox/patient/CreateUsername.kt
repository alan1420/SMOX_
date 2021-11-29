package com.example.smox.patient

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.VolleyError
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import org.json.JSONObject
import com.example.smox.*

class CreateUsername : AppCompatActivity() {
    private var data: Int? = null

    var auth = FirebaseAuth.getInstance()
    var currentUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.p_username)
    }

    override fun onStart() {
        super.onStart()
        currentUser = auth.currentUser
    }

    fun register_p(view: View) {
        val mUsername = findViewById<EditText>(R.id.enterusername).text.toString()
        currentUser?.getIdToken(true)?.addOnSuccessListener {
            //registerData(it.token.toString(), mUsername)
            Log.d("Token ID", it.token.toString())
            val dataJson = JSONObject().put("role", 2)
            dataJson.put("username", mUsername)
            sendData("signup-finalize", it.token.toString(), dataJson, this.applicationContext,
                object : VolleyResult {
                    override fun onSuccess(response: JSONObject) {
                        if (response != null) {
                            val intent = Intent(this@CreateUsername, Homepage::class.java)
                            intent.putExtra("name", response.getString("name"))
                            startActivity(intent)
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                            finish()
                        }
                    }

                    override fun onError(error: VolleyError?) {
                        //TODO("Not yet implemented")
                        Toast.makeText(this@CreateUsername, "Please try again", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
    }

    fun registerData(token: String, username: String?) {
        data?.let { modifyData(token, it, username, this.applicationContext, object:
            VolleyResult {
            override fun onSuccess(response: JSONObject) {
                if (response != null) {
                    ToHomepage(data!!, response.getString("name"))
                }
            }

            override fun onError(error: VolleyError?) {
                TODO("Not yet implemented")
            }
            })
        }
    }
    //Redirect to Caretaker or Patient Homepage
    fun ToHomepage(i: Int, s: String) {
        lateinit var intentHome: Intent
        if (i == 2)
            intentHome = Intent(this, Homepage::class.java)

        intentHome.putExtra("name", s)
        startActivity(intentHome)
        finish()
    }
}