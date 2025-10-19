package com.autoai.app.data.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "documents", indices = [Index(value = ["url"], unique = true)])
data class DocumentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val url: String,
    val domain: String,
    val title: String,
    val content: String,
    val embedding: ByteArray?,
    val updatedAtEpochMs: Long,
)
