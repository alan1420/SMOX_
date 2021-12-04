package com.example.smox

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.android.volley.VolleyError
import com.example.smox.caretaker.Homepage
import com.example.smox.patient.CreateUsername
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONObject

class ConfirmRole : AppCompatActivity() {

    var auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.confirm)
    }

    fun pickPatient(view: View) {
        val clickEffect = AnimationUtils.loadAnimation(this, R.anim.scale_down)
        view.startAnimation(clickEffect)
        val intent = Intent(this, CreateUsername::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    fun pickCaretaker(view: View) {
        val clickEffect = AnimationUtils.loadAnimation(this, R.anim.scale_down)
        view.startAnimation(clickEffect)
        auth.currentUser?.getIdToken(true)?.addOnSuccessListener {
            val dataJson = JSONObject().put("role", 1)
            sendData("signup-finalize", it.token.toString(), dataJson, this.applicationContext,
                object : VolleyResult {
                    override fun onSuccess(response: JSONObject) {
                        if (response != null) {
                            val intent = Intent(this@ConfirmRole, Homepage::class.java)
                            intent.putExtra("name", response.getString("name"))
                            startActivity(intent)
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                            finish()
                        }
                    }

                    override fun onError(error: VolleyError?) {
                        //TODO("Not yet implemented")
                        Toast.makeText(this@ConfirmRole, "Please try again", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }
}