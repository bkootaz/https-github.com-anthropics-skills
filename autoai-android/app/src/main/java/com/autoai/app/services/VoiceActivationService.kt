package com.autoai.app.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.autoai.app.R
import com.autoai.app.features.assistant.AssistantActivity
import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicBoolean

class VoiceActivationService : Service() {
    
    companion object {
        private const val TAG = "VoiceActivationService"
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "voice_activation_channel"
        private const val SAMPLE_RATE = 16000
        private const val BUFFER_SIZE = 1024
        private const val WAKE_WORD_THRESHOLD = 0.7f
    }
    
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val isListening = AtomicBoolean(false)
    private var audioRecord: AudioRecord? = null
    private var wakeWordDetector: WakeWordDetector? = null
    private var powerManager: PowerManager? = null
    private var wakeLock: PowerManager.WakeLock? = null
    
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager?.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "AutoAI::VoiceActivation"
        )
        wakeWordDetector = WakeWordDetector(this)
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, createNotification())
        startVoiceActivation()
        return START_STICKY
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Voice Activation",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Always-on voice activation for AutoAI Assistant"
                setShowBadge(false)
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun createNotification(): Notification {
        val intent = Intent(this, AssistantActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("AutoAI Assistant")
            .setContentText("Listening for wake words...")
            .setSmallIcon(R.drawable.ic_mic)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setSilent(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }
    
    private fun startVoiceActivation() {
        if (isListening.get()) return
        
        serviceScope.launch {
            try {
                isListening.set(true)
                wakeLock?.acquire(10*60*1000L /*10 minutes*/)
                
                initializeAudioRecord()
                startListening()
                
            } catch (e: Exception) {
                Log.e(TAG, "Error starting voice activation", e)
                stopSelf()
            }
        }
    }
    
    private fun initializeAudioRecord() {
        val bufferSize = AudioRecord.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        
        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        )
    }
    
    private suspend fun startListening() {
        val audioRecord = this.audioRecord ?: return
        
        if (audioRecord.state != AudioRecord.STATE_INITIALIZED) {
            Log.e(TAG, "AudioRecord not initialized")
            return
        }
        
        audioRecord.startRecording()
        val buffer = ShortArray(BUFFER_SIZE)
        
        while (isListening.get()) {
            val bytesRead = audioRecord.read(buffer, 0, buffer.size)
            
            if (bytesRead > 0) {
                // Convert to float array for processing
                val floatBuffer = buffer.map { it.toFloat() / Short.MAX_VALUE }.toFloatArray()
                
                // Check for wake words
                val wakeWordDetected = wakeWordDetector?.detectWakeWord(floatBuffer) ?: false
                
                if (wakeWordDetected) {
                    Log.d(TAG, "Wake word detected!")
                    launchAssistant()
                }
            }
            
            delay(100) // Small delay to prevent excessive CPU usage
        }
    }
    
    private fun launchAssistant() {
        val intent = Intent(this, AssistantActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or 
                   Intent.FLAG_ACTIVITY_CLEAR_TOP or
                   Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("wake_word_triggered", true)
        }
        startActivity(intent)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        isListening.set(false)
        audioRecord?.stop()
        audioRecord?.release()
        wakeLock?.release()
        serviceScope.cancel()
    }
}

class WakeWordDetector(private val context: Context) {
    
    private val wakeWords = listOf(
        "hey assistant",
        "hello assistant", 
        "okay assistant",
        "wake up assistant"
    )
    
    fun detectWakeWord(audioData: FloatArray): Boolean {
        // Simple energy-based detection for demo
        // In production, use a proper wake word detection library like Porcupine or Picovoice
        
        val energy = audioData.map { it * it }.average()
        
        if (energy > 0.01) { // Threshold for speech detection
            // For demo purposes, randomly trigger wake word detection
            // In production, implement proper speech recognition
            return Math.random() < 0.001 // Very low probability for demo
        }
        
        return false
    }
}