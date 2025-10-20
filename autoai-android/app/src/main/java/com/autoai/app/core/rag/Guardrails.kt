package com.autoai.app.core.rag

object Guardrails {
    private val riskyPatterns = listOf(
        Regex("(?i)explosive|weapon|harm|illegal"),
    )

    fun isSafe(text: String): Boolean = riskyPatterns.none { it.containsMatchIn(text) }
}
