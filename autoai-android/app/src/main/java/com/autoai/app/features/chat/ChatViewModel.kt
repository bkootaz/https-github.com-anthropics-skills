package com.autoai.app.features.chat

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.autoai.app.core.rag.RagOrchestrator
import com.autoai.app.data.SettingsStore
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first

class ChatViewModel(app: Application) : AndroidViewModel(app) {
    private val settingsStore = SettingsStore(app)
    private val rag = RagOrchestrator()

    fun answer(query: String): String {
        val settings = runBlocking { settingsStore.settingsFlow().first() }
        val sys = settings?.systemInstructions ?: "Act as an automotive parts and diagnostics assistant for Ford Falcons and Holden Commodores. Cite sources. Abstain if unsure."
        return rag.answerSync(query, sys)
    }
}
