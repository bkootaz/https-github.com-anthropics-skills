package com.autoai.app.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.*

class TextToSpeechService : Service(), TextToSpeech.OnInitListener {
    
    companion object {
        private const val TAG = "TextToSpeechService"
    }
    
    private var textToSpeech: TextToSpeech? = null
    private var isInitialized = false
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    override fun onCreate() {
        super.onCreate()
        textToSpeech = TextToSpeech(this, this)
    }
    
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = textToSpeech?.setLanguage(Locale.getDefault())
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e(TAG, "Language not supported")
            } else {
                isInitialized = true
                Log.d(TAG, "TextToSpeech initialized successfully")
            }
        } else {
            Log.e(TAG, "TextToSpeech initialization failed")
        }
    }
    
    fun speak(text: String, onComplete: (() -> Unit)? = null) {
        if (!isInitialized) {
            Log.w(TAG, "TextToSpeech not initialized")
            onComplete?.invoke()
            return
        }
        
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        
        // Estimate speaking time and call onComplete
        val estimatedTime = text.length * 50L // Rough estimate: 50ms per character
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            onComplete?.invoke()
        }, estimatedTime)
    }
    
    fun stop() {
        textToSpeech?.stop()
    }
    
    fun isSpeaking(): Boolean {
        return textToSpeech?.isSpeaking ?: false
    }
    
    override fun onDestroy() {
        super.onDestroy()
        textToSpeech?.stop()
        textToSpeech?.shutdown()
    }
}