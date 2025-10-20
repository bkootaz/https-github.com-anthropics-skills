package com.autoai.app.core.embedding

import android.content.Context
import ai.onnxruntime.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OnnxEmbedder(context: Context) : AutoCloseable {
    private val env: OrtEnvironment = OrtEnvironment.getEnvironment()
    private var session: OrtSession? = null

    suspend fun loadModelFromAsset(assetName: String, ctx: Context) = withContext(Dispatchers.IO) {
        val bytes = ctx.assets.open(assetName).use { it.readBytes() }
        session?.close()
        session = env.createSession(bytes)
    }

    suspend fun embed(texts: List<String>): List<FloatArray> = withContext(Dispatchers.Default) {
        // NOTE: This is a stub. Replace with tokenizer + model inputs
        // For now, return zero vectors to allow wiring
        val dim = 384
        return@withContext texts.map { FloatArray(dim) { 0f } }
    }

    override fun close() { session?.close() }
}
