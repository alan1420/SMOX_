package com.example.smox.caretaker

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.view.ViewGroup


import android.view.LayoutInflater
import android.widget.Toast
import com.android.volley.VolleyError
import com.example.smox.*
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONObject


class PatientList : AppCompatActivity() {
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.c_patients_list)

        val vi = applicationContext.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater

        // fill in any details dynamically here
        var prev_id = 0

        val user = auth.currentUser

        if (user != null) {
            user.getIdToken(true).addOnSuccessListener {
                Log.d("Token ID", it.token.toString()) // token #1
                sendDataGET("show-patient", it.token.toString(), this.applicationContext,
                    object : VolleyResult {
                        override fun onSuccess(response: JSONObject) {
                            if (response != null) {
                                if (!response.has("empty")) {
                                    val array = response.getJSONArray("data")
                                    for (i in 0 until array.length()) {
                                        val item = array.getJSONObject(i)

                                        // fill in any details dynamically here
                                        val v: View = vi.inflate(R.layout.c_patients_card, null)
                                        v.findViewById<TextView>(R.id.fullname).text = item.getString("fullname")
                                        v.findViewById<TextView>(R.id.birthday).text = item.getString("birthday")
                                        v.findViewById<TextView>(R.id.email).text = item.getString("email")
                                        v.findViewById<TextView>(R.id.phoneNumber).text = item.getString("phoneNumber")

                                        val rev = v.findViewById<LinearLayout>(R.id.patient_card)
                                        val p = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT)
                                        v.id = View.generateViewId()

                                        val insertPoint = findViewById<LinearLayout>(R.id.patients)
                                        //var f: MarginLayoutParams = v.layoutParams as MarginLayoutParams
                                        //f.setMargins(20,10,20,0)
                                        insertPoint.addView(v,
                                            i, p)
                                        //prev_id = rev.id
                                    }

                                    println(response)
                                }
                            }
                        }

                        override fun onError(error: VolleyError?) {
                            if (error != null)
                                println(error)
                                Toast.makeText(this@PatientList, "Network error, please check your connection!", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
        }
    }

    fun back(view: View) {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}