package com.example.smox

import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        Log.d("FCM", "Refreshed token: $token")

        val edit = getSharedPreferences("smox", MODE_PRIVATE).edit()
        edit.putString("fcm_token", token)
        edit.putBoolean("fcm_sync", false)
        edit.apply()
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