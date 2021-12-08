package com.example.smox.caretaker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.VolleyError
import com.example.smox.*
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.c_menu.*
import org.json.JSONObject

class ConnectPatient : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.c_connect)

    }

    fun connect(view: View) {
        val username = findViewById<EditText>(R.id.patient_username).text.toString()
        if (textIsNotEmpty(username)) {
            auth.currentUser?.getIdToken(true)?.addOnSuccessListener {
                sendDataGET("checkUsername/$username", it.token.toString(),this,
                    object : VolleyResult {
                        override fun onSuccess(response: JSONObject) {
                            if (!response.has("message")) {
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                                val intent = Intent(this@ConnectPatient, ConfirmPatient::class.java)
                                //intent.putExtra("patient_data", )
                                val bytes: ByteArray = response.toString().toByteArray()
                                intent.putExtra("patient_data", bytes)
                                startActivity(intent)
                                val clickEffect = AnimationUtils.loadAnimation(this@ConnectPatient, R.anim.scale_down)
                                view.startAnimation(clickEffect)
                            } else
                                Toast.makeText(this@ConnectPatient,
                                    "Patient already assigned to another Caretaker!",
                                    Toast.LENGTH_SHORT).show()
                        }

                        override fun onError(error: VolleyError?) {
                            if (error != null) {
                                Log.d("Volley", getVolleyError(error))
                                Toast.makeText(this@ConnectPatient,
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