package com.autoai.app.features.assistant

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.autoai.app.ui.theme.AutoAITheme
import kotlinx.coroutines.delay
import java.util.*

class AssistantActivity : ComponentActivity(), TextToSpeech.OnInitListener {
    
    private var speechRecognizer: SpeechRecognizer? = null
    private var textToSpeech: TextToSpeech? = null
    private var isListening = false
    private var isSpeaking = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Override lock screen
        setupLockScreenOverride()
        
        // Initialize TTS
        textToSpeech = TextToSpeech(this, this)
        
        // Initialize speech recognizer
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        
        setContent {
            AutoAITheme {
                AssistantScreen(
                    onMicClick = { toggleListening() },
                    onCloseClick = { finish() }
                )
            }
        }
    }
    
    private fun setupLockScreenOverride() {
        // Show on lock screen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }
        
        // Dismiss keyguard if possible
        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            keyguardManager.requestDismissKeyguard(this, null)
        }
        
        // Keep screen on
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
    
    private fun toggleListening() {
        if (isListening) {
            stopListening()
        } else {
            startListening()
        }
    }
    
    private fun startListening() {
        if (speechRecognizer == null) return
        
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...")
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }
        
        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                isListening = true
            }
            
            override fun onBeginningOfSpeech() {}
            
            override fun onRmsChanged(rmsdB: Float) {}
            
            override fun onBufferReceived(buffer: ByteArray?) {}
            
            override fun onEndOfSpeech() {
                isListening = false
            }
            
            override fun onError(error: Int) {
                isListening = false
                when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> speak("Audio error occurred")
                    SpeechRecognizer.ERROR_CLIENT -> speak("Client error occurred")
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> speak("Microphone permission required")
                    SpeechRecognizer.ERROR_NETWORK -> speak("Network error occurred")
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> speak("Network timeout")
                    SpeechRecognizer.ERROR_NO_MATCH -> speak("No speech recognized")
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> speak("Recognizer busy")
                    SpeechRecognizer.ERROR_SERVER -> speak("Server error occurred")
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> speak("Speech timeout")
                }
            }
            
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val spokenText = matches[0]
                    processVoiceCommand(spokenText)
                }
                isListening = false
            }
            
            override fun onPartialResults(partialResults: Bundle?) {}
            
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
        
        speechRecognizer?.startListening(intent)
    }
    
    private fun stopListening() {
        speechRecognizer?.stopListening()
        isListening = false
    }
    
    private fun processVoiceCommand(command: String) {
        speak("I heard: $command")
        
        // Process the command with Firebase AI
        // This would integrate with your Firebase Functions
        when (command.lowercase()) {
            "hello" -> speak("Hello! How can I help you today?")
            "what time is it" -> speak("The current time is ${java.text.DateFormat.getTimeInstance().format(Date())}")
            "goodbye" -> {
                speak("Goodbye! Have a great day!")
                finish()
            }
            else -> {
                // Send to Firebase for AI processing
                processWithFirebaseAI(command)
            }
        }
    }
    
    private fun processWithFirebaseAI(command: String) {
        // This would integrate with Firebase Functions
        // For now, provide a simple response
        speak("I'm processing your request: $command")
    }
    
    private fun speak(text: String) {
        if (textToSpeech?.isSpeaking == true) {
            textToSpeech?.stop()
        }
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        isSpeaking = true
        
        // Stop speaking indicator after estimated time
        val estimatedTime = text.length * 50L // Rough estimate
        lifecycleScope.launch {
            delay(estimatedTime)
            isSpeaking = false
        }
    }
    
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech?.language = Locale.getDefault()
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer?.destroy()
        textToSpeech?.stop()
        textToSpeech?.shutdown()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssistantScreen(
    onMicClick: () -> Unit,
    onCloseClick: () -> Unit
) {
    var isListening by remember { mutableStateOf(false) }
    var isSpeaking by remember { mutableStateOf(false) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.9f))
    ) {
        // Close button
        IconButton(
            onClick = onCloseClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Text(
                text = "✕",
                color = Color.White,
                fontSize = 24.sp
            )
        }
        
        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Assistant title
            Text(
                text = "AutoAI Assistant",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Microphone button
            FloatingActionButton(
                onClick = {
                    isListening = !isListening
                    onMicClick()
                },
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape),
                containerColor = if (isListening) Color.Red else Color.White,
                contentColor = if (isListening) Color.White else Color.Black
            ) {
                Icon(
                    imageVector = if (isListening) Icons.Default.MicOff else Icons.Default.Mic,
                    contentDescription = if (isListening) "Stop listening" else "Start listening",
                    modifier = Modifier.size(48.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Status text
            Text(
                text = when {
                    isListening -> "Listening..."
                    isSpeaking -> "Speaking..."
                    else -> "Tap to speak"
                },
                color = Color.White,
                fontSize = 18.sp
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Instructions
            Text(
                text = "Say 'Hey Assistant' or 'Hello Assistant' to activate",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp
            )
        }
    }
}