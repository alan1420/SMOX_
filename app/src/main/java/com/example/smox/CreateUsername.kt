package com.example.smox

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.content.Intent
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.example.smox.patient.Homepage
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONObject

class CreateUsername : AppCompatActivity() {
    private var data: Int? = null

    // [START declare_auth]
    var auth = FirebaseAuth.getInstance()
    // [END declare_auth]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.p_username)
        data = intent.getIntExtra("role", 2)
    }

    fun register_p(view: View) {
        val currentUser = auth.currentUser
        val mUsername = findViewById<EditText>(R.id.enterusername).text.toString()
        if (currentUser != null) {
            currentUser.getIdToken(true).addOnSuccessListener {
                registerData(it.token.toString(), mUsername)
                Log.d("Token ID", it.token.toString())
            }
        }
    }

    fun registerData(token: String, username: String) {
        val url = "http://192.168.0.88/smox/public/api/signup-finalize"
        val dataJson = JSONObject()
        dataJson.put("role", data)
        dataJson.put("username", username)
        println(dataJson)

        val jsonObjectRequest = object: JsonObjectRequest(Method.POST, url, dataJson,
            Response.Listener<JSONObject> { response ->
                //Toast.makeText(this, "Username & Role done!!", Toast.LENGTH_SHORT).show()
                println(response.toString())
                val intent = Intent(this, Homepage::class.java)
                intent.putExtra("name", response.getString("name"))
                startActivity(intent)
                finish()
            },
            Response.ErrorListener { error ->
                //TODO: Handle error
                println("Error volley!!!")
                println(error.message)
            }
        ){
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $token"
                return headers
            }
        }
        val queue = VolleySingleton.getInstance(this.applicationContext).requestQueue
        queue.add(jsonObjectRequest)
        //VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}