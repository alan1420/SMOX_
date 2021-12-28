package com.example.smox

import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.google.gson.JsonObject

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        Log.d("FCM", "Refreshed token: $token")

        val newData = Gson().fromJson("{}", JsonObject::class.java)
        newData.addProperty("fcm_token", token)
        newData.addProperty("sync", false)
        createFile(this,
            "store_token_fcm.json", newData.toString())
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        //sendRegistrationToServer(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("FCM", "Received data " + remoteMessage.data.toString())
        if (remoteMessage.data.isNotEmpty())
            passMessageToActivity(remoteMessage.data)
    }

    private fun passMessageToActivity(messageReceived: MutableMap<String, String>) {
        val jsonData = Gson().toJson(messageReceived)
        println(jsonData)
        val intent = Intent().apply{
            action = "INTENT_ACTION_SEND_MESSAGE"
            putExtra("fcm", jsonData.toByteArray())
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }
}