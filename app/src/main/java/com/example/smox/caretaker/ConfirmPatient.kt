package com.example.smox.caretaker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.VolleyError
import com.example.smox.*
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.JsonObject
import org.json.JSONObject

class ConfirmPatient : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()

    lateinit var data: JSONObject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.c_confirm_patient)
        data = JSONObject(String(intent.getByteArrayExtra("patient_data")!!))
        findViewById<TextView>(R.id.fullname).setText(data.getString("fullname").toString())
        findViewById<TextView>(R.id.birthday).setText(data.getString("birthday").toString())
        findViewById<TextView>(R.id.email).setText(data.getString("email").toString())
        findViewById<TextView>(R.id.phoneNumber).setText(data.getString("phoneNumber").toString())
    }

    fun confirm(view: View) {
        auth.currentUser?.getIdToken(true)?.addOnSuccessListener {
            val username = data.getInt("patient_id")
            sendDataGET("add-patient/$username", it.token.toString(),this,
                object : VolleyResult {
                    override fun onSuccess(response: JSONObject) {
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            val intent = Intent(this@ConfirmPatient, Homepage::class.java)
                            startActivity(intent)
                            Toast.makeText(this@ConfirmPatient, "Patient Added", Toast.LENGTH_SHORT).show()
                            val clickEffect = AnimationUtils.loadAnimation(this@ConfirmPatient, R.anim.scale_down)
                            view.startAnimation(clickEffect)
                            finish()
                    }
                    override fun onError(error: VolleyError?) {
                        if (error != null) {
                            Log.d("Volley", getVolleyError(error))
                            Toast.makeText(this@ConfirmPatient,
                                getVolleyError(error),
                                Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            )
        }

    }
}