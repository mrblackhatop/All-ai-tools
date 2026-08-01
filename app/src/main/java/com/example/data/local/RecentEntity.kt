package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_history")
data class RecentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val toolId: String,
    val toolTitle: String,
    val promptText: String,
    val resultText: String,
    val languageCode: String,
    val timestamp: Long = System.currentTimeMillis()
)
