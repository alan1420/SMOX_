package com.example.smox


import android.content.Context
import android.content.Intent
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject
import com.android.volley.DefaultRetryPolicy
import com.android.volley.toolbox.JsonArrayRequest
import com.example.smox.patient.Homepage
import org.json.JSONArray


//Interface
interface VolleyResult {
    fun onSuccess(response: JSONObject)
    fun onError(error: VolleyError?)
}

fun modifyData(token: String, role: Int, username: String?, context: Context, result: VolleyResult) {
    val url = "http://103.146.34.5/smox/public/api/signup-finalize"
    val dataJson = JSONObject()
    dataJson.put("role", role)
    if (username != null)
        dataJson.put("username", username)
    println(dataJson)

    val jsonObjectRequest = object: JsonObjectRequest(Method.POST, url, dataJson,
        Response.Listener<JSONObject> { response ->
            result.onSuccess(response)
        },
        Response.ErrorListener { error ->
            //TODO: Handle error
            result.onError(error)
        }
    ){
        override fun getHeaders(): MutableMap<String, String> {
            val headers = HashMap<String, String>()
            headers["Authorization"] = "Bearer $token"
            return headers
        }
    }
    jsonObjectRequest.retryPolicy = DefaultRetryPolicy(5000,
        5, 1F)
    val queue = VolleySingleton.getInstance(context).requestQueue
    queue.add(jsonObjectRequest)
}

fun sendData(path: String, token: String, dataJson: JSONObject, context: Context, result: VolleyResult) {
    val url = "http://103.146.34.5/smox/public/api/$path"
    println(dataJson)

    val jsonObjectRequest = object: JsonObjectRequest(Method.POST, url, dataJson,
        Response.Listener<JSONObject> { response ->
            result.onSuccess(response)
        },
        Response.ErrorListener { error ->
            //TODO: Handle error
            result.onError(error)
        }
    ){
        override fun getHeaders(): MutableMap<String, String> {
            val headers = HashMap<String, String>()
            headers["Authorization"] = "Bearer $token"
            return headers
        }
    }
    jsonObjectRequest.retryPolicy = DefaultRetryPolicy(5000,
        5, 1F)
    val queue = VolleySingleton.getInstance(context).requestQueue
    queue.add(jsonObjectRequest)
}

fun sendDataGET(path: String, token: String, context: Context, result: VolleyResult) {
    val url = "http://192.168.0.88/smox/public/api/$path"
    val jsonObjectRequest = object: JsonObjectRequest(Method.GET, url, null,
        Response.Listener<JSONObject> { response ->
            result.onSuccess(response)
        },
        Response.ErrorListener { error ->
            //TODO: Handle error
            result.onError(error)
        }
    ){
        override fun getHeaders(): MutableMap<String, String> {
            val headers = HashMap<String, String>()
            headers["Authorization"] = "Bearer $token"
            return headers
        }
    }
    jsonObjectRequest.retryPolicy = DefaultRetryPolicy(5000,
        5, 1F)
    val queue = VolleySingleton.getInstance(context).requestQueue
    queue.add(jsonObjectRequest)
}

fun toHomePage(context: Context, role: Int, name: String?) {
    //To Patient
    if (role == 2) {
        val intent = Intent(context, Homepage::class.java)
        intent.putExtra("name", name)
        context.startActivity(intent)
        //finish()
    }
    //To Caretaker
    else {
        val intent = Intent(context, com.example.smox.caretaker.Homepage::class.java)
        intent.putExtra("name", name)
        context.startActivity(intent)
        //finish()
    }
}