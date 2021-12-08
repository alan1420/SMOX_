package com.example.smox.caretaker

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.view.ViewGroup


import android.view.LayoutInflater
import android.view.animation.AnimationUtils
import android.widget.RelativeLayout
import android.widget.Toast
import com.android.volley.VolleyError
import com.example.smox.*
//import com.example.smox.patient.Homepage
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

        val user = auth.currentUser

        var data = ""

        if (user != null) {
            user.getIdToken(true).addOnSuccessListener {
                Log.d("Token ID", it.token.toString()) // token #1
                sendDataGET("show-patient", it.token.toString(), this.applicationContext,
                    object : VolleyResult {
                        override fun onSuccess(response: JSONObject) {
                            if (response != null) {
                                if (!response.has("empty")) {
                                    if (intent.hasExtra("current_patient")) {
                                        data = intent.getStringExtra("current_patient").toString()
                                    }
                                    val array = response.getJSONArray("data")
                                    var index1 = 0
                                    for (i in 0 until array.length()) {
                                        val item = array.getJSONObject(i)

                                        //Skipping current patient
                                        if (data == item.getString("fullname").toString())
                                            continue

                                        // fill in any details dynamically here

                                        val v: View = vi.inflate(R.layout.c_patients_card, null)
                                        val rlClick = v.findViewById<View>(R.id.rl_click) as RelativeLayout
                                        rlClick.setOnClickListener(View.OnClickListener { view ->
                                            clickCard(view, item.getInt("patient_id"))
                                        })
                                        v.findViewById<TextView>(R.id.fullname).text = item.getString("fullname")
                                        v.findViewById<TextView>(R.id.birthday).text = item.getString("birthday")
                                        v.findViewById<TextView>(R.id.email).text = item.getString("email")
                                        v.findViewById<TextView>(R.id.phoneNumber).text = item.getString("phoneNumber")

                                        val p = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT)
                                        v.id = View.generateViewId()

                                        val insertPoint = findViewById<LinearLayout>(R.id.patients)
                                        insertPoint.addView(v,
                                            index1++, p)
                                    }
                                }
                            }
                        }

                        override fun onError(error: VolleyError?) {
                            if (error != null) {
                                Log.d("Volley", getVolleyError(error))
                                Toast.makeText(this@PatientList,
                                    getVolleyError(error),
                                    Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                )
            }
        }
    }

    fun clickCard(view: View, id: Int) {
        val clickEffect = AnimationUtils.loadAnimation(this, R.anim.scale_up)
        view.startAnimation(clickEffect)
        println(id)
        val intent = Intent(this, Homepage::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("patientId", id)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}