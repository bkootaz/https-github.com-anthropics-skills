package com.autoai.app.core.rag

import java.nio.ByteBuffer
import java.nio.ByteOrder

object VectorUtils {
    fun floatArrayToByteArray(values: FloatArray): ByteArray {
        val buffer = ByteBuffer.allocate(values.size * 4).order(ByteOrder.LITTLE_ENDIAN)
        values.forEach { buffer.putFloat(it) }
        return buffer.array()
    }

    fun byteArrayToFloatArray(bytes: ByteArray): FloatArray {
        val buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
        val floats = FloatArray(bytes.size / 4)
        var i = 0
        while (buffer.hasRemaining()) {
            floats[i++] = buffer.getFloat()
        }
        return floats
    }

    fun cosineSimilarity(a: FloatArray, b: FloatArray): Float {
        var dot = 0.0
        var na = 0.0
        var nb = 0.0
        val n = minOf(a.size, b.size)
        for (i in 0 until n) {
            val av = a[i].toDouble(); val bv = b[i].toDouble()
            dot += av * bv
            na += av * av
            nb += bv * bv
        }
        if (na == 0.0 || nb == 0.0) return 0f
        return (dot / (kotlin.math.sqrt(na) * kotlin.math.sqrt(nb))).toFloat()
    }
}
