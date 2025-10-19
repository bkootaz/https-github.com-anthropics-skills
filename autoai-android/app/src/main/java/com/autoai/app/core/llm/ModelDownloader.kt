package com.autoai.app.core.llm

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL

class ModelDownloader(private val context: Context) {
    suspend fun download(url: String, fileName: String): File = withContext(Dispatchers.IO) {
        val dir = File(context.filesDir, "models").apply { mkdirs() }
        val out = File(dir, fileName)
        if (out.exists() && out.length() > 0) return@withContext out
        URL(url).openStream().use { input ->
            out.outputStream().use { output -> input.copyTo(output) }
        }
        out
    }
}
