package com.autoai.app.services

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseMessagingService : FirebaseMessagingService() {
    
    companion object {
        private const val TAG = "FirebaseMsgService"
    }
    
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        
        Log.d(TAG, "From: ${remoteMessage.from}")
        
        // Handle data payload
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
            handleDataMessage(remoteMessage.data)
        }
        
        // Handle notification payload
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
            handleNotificationMessage(it.title, it.body)
        }
    }
    
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed token: $token")
        
        // Send token to your server
        sendTokenToServer(token)
    }
    
    private fun handleDataMessage(data: Map<String, String>) {
        when (data["type"]) {
            "voice_response" -> {
                // Handle voice response from AI
                val response = data["response"]
                val shouldSpeak = data["speak"]?.toBoolean() ?: false
                
                if (shouldSpeak && !response.isNullOrEmpty()) {
                    // Use TTS to speak the response
                    speakResponse(response)
                }
            }
            "wake_up" -> {
                // Wake up the assistant
                wakeUpAssistant()
            }
            else -> {
                Log.d(TAG, "Unknown message type: ${data["type"]}")
            }
        }
    }
    
    private fun handleNotificationMessage(title: String?, body: String?) {
        // Handle notification messages
        Log.d(TAG, "Notification - Title: $title, Body: $body")
    }
    
    private fun sendTokenToServer(token: String) {
        // Send the FCM token to your server
        // This allows your server to send messages to this device
        Log.d(TAG, "Sending token to server: $token")
    }
    
    private fun speakResponse(response: String) {
        // This would integrate with your TTS service
        Log.d(TAG, "Speaking response: $response")
    }
    
    private fun wakeUpAssistant() {
        // This would launch the assistant activity
        Log.d(TAG, "Waking up assistant")
    }
}