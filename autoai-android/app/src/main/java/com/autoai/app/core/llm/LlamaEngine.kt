package com.autoai.app.core.llm

import android.content.Context

class LlamaEngine(private val context: Context) {
    fun generate(systemPrompt: String, messages: List<Pair<String, String>>): String {
        // Stub implementation. Replace with llama.cpp JNI binding calls.
        val last = messages.lastOrNull()?.second ?: ""
        return "[offline LLM stub] $last"
    }
}
