package com.autoai.app.core.rag

import com.autoai.app.data.db.DocumentEntity

class VectorIndex {
    fun topK(query: FloatArray, docs: List<DocumentEntity>, k: Int): List<Pair<DocumentEntity, Float>> {
        val scored = docs.mapNotNull { d ->
            val emb = d.embedding ?: return@mapNotNull null
            val vec = VectorUtils.byteArrayToFloatArray(emb)
            val score = VectorUtils.cosineSimilarity(query, vec)
            d to score
        }
        return scored.sortedByDescending { it.second }.take(k)
    }
}
