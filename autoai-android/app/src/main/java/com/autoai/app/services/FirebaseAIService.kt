package com.autoai.app.services

import android.util.Log
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import org.json.JSONObject

class FirebaseAIService {
    
    companion object {
        private const val TAG = "FirebaseAIService"
        private const val AI_FUNCTION_NAME = "processVoiceCommand"
    }
    
    private val functions: FirebaseFunctions = Firebase.functions
    
    suspend fun processVoiceCommand(
        command: String,
        context: Map<String, Any> = emptyMap()
    ): AIResponse {
        return try {
            val data = hashMapOf(
                "command" to command,
                "context" to context,
                "timestamp" to System.currentTimeMillis()
            )
            
            val result = functions
                .getHttpsCallable(AI_FUNCTION_NAME)
                .call(data)
                .await()
            
            val resultData = result.data as? Map<String, Any>
            parseAIResponse(resultData)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error processing voice command", e)
            AIResponse(
                text = "Sorry, I encountered an error processing your request.",
                shouldSpeak = true,
                action = null,
                confidence = 0.0
            )
        }
    }
    
    private fun parseAIResponse(data: Map<String, Any>?): AIResponse {
        return if (data != null) {
            AIResponse(
                text = data["text"] as? String ?: "I didn't understand that.",
                shouldSpeak = data["shouldSpeak"] as? Boolean ?: true,
                action = data["action"] as? String,
                confidence = (data["confidence"] as? Number)?.toDouble() ?: 0.0
            )
        } else {
            AIResponse(
                text = "I didn't understand that.",
                shouldSpeak = true,
                action = null,
                confidence = 0.0
            )
        }
    }
}

data class AIResponse(
    val text: String,
    val shouldSpeak: Boolean,
    val action: String?,
    val confidence: Double
)