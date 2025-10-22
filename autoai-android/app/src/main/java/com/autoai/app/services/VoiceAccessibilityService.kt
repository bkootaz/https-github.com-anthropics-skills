package com.autoai.app.services

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent

class VoiceAccessibilityService : AccessibilityService() {
    
    companion object {
        private const val TAG = "VoiceAccessibilityService"
    }
    
    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "Voice Accessibility Service Connected")
        
        // Start the voice activation service
        val intent = Intent(this, VoiceActivationService::class.java)
        startForegroundService(intent)
    }
    
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // This service is primarily for keeping the voice activation service running
        // and handling system-level events that might affect voice activation
        event?.let {
            when (it.eventType) {
                AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                    // Handle window state changes
                    Log.d(TAG, "Window state changed: ${it.packageName}")
                }
                AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED -> {
                    // Handle notification changes
                    Log.d(TAG, "Notification state changed")
                }
                else -> {
                    // Handle other accessibility events if needed
                }
            }
        }
    }
    
    override fun onInterrupt() {
        Log.d(TAG, "Voice Accessibility Service Interrupted")
    }
    
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Voice Accessibility Service Destroyed")
    }
}