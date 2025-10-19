package com.autoai.app.core.rag

import com.autoai.app.core.llm.LlamaEngine
import com.autoai.app.data.db.DocumentEntity

class RagOrchestrator {
    private val llm: LlamaEngine? = null // wire later

    fun answerSync(query: String, systemInstructions: String): String {
        // Placeholder retrieval result and abstention policy (to be wired to DB and embeddings)
        val retrieved: List<DocumentEntity> = emptyList()
        if (retrieved.isEmpty()) {
            return "I don't have enough verified sources to answer that. Try refining the query (model, year, engine code) or sync sources."
        }
        val context = retrieved.joinToString("\n\n") { "${it.title} (${it.url})\n${it.content.take(600)}" }
        val messages = listOf(
            "system" to systemInstructions,
            "user" to "Question: $query\n\nContext (cited):\n$context\n\nProvide a concise answer with citations."
        )
        return llm?.generate(systemInstructions, messages) ?: "Stub: retrieved ${retrieved.size} passages"
    }
}
