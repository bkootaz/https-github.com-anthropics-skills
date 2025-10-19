package com.autoai.app.data.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface DocumentDao {
    @Upsert
    suspend fun upsert(document: DocumentEntity): Long

    @Query("SELECT * FROM documents")
    suspend fun getAll(): List<DocumentEntity>

    @Query("SELECT * FROM documents WHERE domain IN (:domains)")
    suspend fun getByDomains(domains: List<String>): List<DocumentEntity>

    @Query("DELETE FROM documents WHERE domain NOT IN (:domains)")
    suspend fun deleteNotInDomains(domains: List<String>)
}
