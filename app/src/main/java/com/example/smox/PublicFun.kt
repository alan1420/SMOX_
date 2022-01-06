package com.example.smox

import android.content.Context
import android.content.Intent
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject
import com.example.smox.patient.Homepage
import com.google.firebase.auth.FirebaseAuth
import org.apache.http.conn.ConnectTimeoutException
import org.json.JSONException
import org.xmlpull.v1.XmlPullParserException
import java.io.*
import java.lang.StringBuilder
import java.net.ConnectException
import java.net.MalformedURLException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.time.LocalDateTime
import java.time.ZoneId

//Interface
interface VolleyResult {
    fun onSuccess(response: JSONObject)
    fun onError(error: VolleyError?)
}

interface TokenResult {
    fun onSuccess(token: String)
}

val ServerIP = "103.146.34.6"
//val ServerIP = "192.168.0.88"

fun sendDataPOST(path: String, token: String, dataJson: JSONObject, context: Context, result: VolleyResult) {
    val url = "http://$ServerIP/smox/public/api/$path"

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
    //jsonObjectRequest.retryPolicy = DefaultRetryPolicy(5000,
    //    5, 1F)
    val queue = VolleySingleton.getInstance(context).requestQueue
    jsonObjectRequest.setShouldCache(false)
    queue.add(jsonObjectRequest)
}

fun sendDataGET(path: String, token: String, context: Context, result: VolleyResult) {
    val url = "http://$ServerIP/smox/public/api/$path"
    val jsonObjectRequest = object: JsonObjectRequest(Method.GET, url, null,
        Response.Listener<JSONObject> { response ->
            result.onSuccess(response)
        },
        Response.ErrorListener { error ->
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
    jsonObjectRequest.setShouldCache(false)
    val queue = VolleySingleton.getInstance(context).requestQueue
    queue.add(jsonObjectRequest)
}

fun getVolleyError(error: VolleyError): String {
    var errorMsg = ""
    if (error is NoConnectionError) {
        errorMsg = "Your device is not connected to internet.please try again with active internet connection"

    } else if (error is NetworkError || error.cause is ConnectException) {
        errorMsg = "Your device is not connected to internet.please try again with active internet connection"
    } else if (error.cause is MalformedURLException) {
        errorMsg = "That was a bad request please try again…"
    } else if (error is ParseError || error.cause is IllegalStateException || error.cause is JSONException || error.cause is XmlPullParserException) {
        errorMsg = "There was an error parsing data…"
    } else if (error.cause is OutOfMemoryError) {
        errorMsg = "Device out of memory"
    } else if (error is AuthFailureError) {
        errorMsg = "Failed to authenticate user at the server, please contact support"
    } else if (error is ServerError || error.cause is ServerError) {
        errorMsg = "Internal server error occurred please try again...."
    } else if (error is TimeoutError || error.cause is SocketTimeoutException || error.cause is ConnectTimeoutException || error.cause is SocketException || (error.cause!!.message != null && error.cause!!.message!!.contains(
            "Your connection has timed out, please try again"
        ))
    ) {
        errorMsg = "Your connection has timed out, please try again"
    } else {
        errorMsg = "An unknown error occurred during the operation, please try again"
    }
    return errorMsg
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

fun getToken(context: Context, result: TokenResult) {
    val auth = FirebaseAuth.getInstance()
    val firebaseUser = auth.currentUser
    if (firebaseUser != null) {
        val sharedPref = context.getSharedPreferences("smox", Context.MODE_PRIVATE)
        val authToken = sharedPref.getString("auth_token", null)
        val authExpire = sharedPref.getLong("auth_expire", 69)
        if (authExpire.compareTo(69) != 0) {
            val zoneId: ZoneId = ZoneId.systemDefault()
            val current = LocalDateTime.now().plusMinutes(30).atZone(zoneId).toEpochSecond()
            //If token not expired
            if (current < authExpire) {
                println("Using old token, expired at: " + authExpire)
                authToken?.let { result.onSuccess(it) }
                return
            }
        }
        firebaseUser.getIdToken(true).addOnSuccessListener{
            println("Generating token, expired at: " + it.expirationTimestamp)
            val edit = sharedPref.edit()
            edit.putString("auth_token", it.token.toString())
            edit.putLong("auth_expire", it.expirationTimestamp)
            edit.apply()
            result.onSuccess(it.token.toString())
        }
    }
}

fun readFile(context: Context, fileName: String): String? {
    return try {
        val fis: FileInputStream = context.openFileInput(fileName)
        val isr = InputStreamReader(fis)
        val bufferedReader = BufferedReader(isr)
        val sb = StringBuilder()
        var line: String?
        while (bufferedReader.readLine().also { line = it } != null) {
            sb.append(line)
        }
        sb.toString()
    } catch (fileNotFound: FileNotFoundException) {
        null
    } catch (ioException: IOException) {
        null
    }
}

fun createFile(context: Context, fileName: String, jsonString: String?): Boolean {
    return try {
        val fos = context.openFileOutput(fileName, Context.MODE_PRIVATE)
        if (jsonString != null) {
            fos.write(jsonString.toByteArray())
        }
        fos.close()
        true
    } catch (fileNotFound: FileNotFoundException) {
        false
    } catch (ioException: IOException) {
        false
    }
}

fun isFilePresent(context: Context, fileName: String): Boolean {
    val path = context.filesDir.absolutePath + "/" + fileName
    val file = File(path)
    return file.exists()
}

fun textIsNotEmpty(text: String) :Boolean {
    return text.trim().isNotEmpty()
}

fun textIsEmpty(text: String) :Boolean {
    return text.trim().isEmpty()
}
