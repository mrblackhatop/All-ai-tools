package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ToolDao {
    @Query("SELECT toolId FROM favorites")
    fun getAllFavoriteIds(): Flow<List<String>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE toolId = :toolId)")
    suspend fun isFavorite(toolId: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE toolId = :toolId")
    suspend fun removeFavorite(toolId: String)

    @Query("SELECT * FROM recent_history ORDER BY timestamp DESC LIMIT 50")
    fun getRecentHistory(): Flow<List<RecentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addRecent(recent: RecentEntity)

    @Query("DELETE FROM recent_history WHERE id = :id")
    suspend fun deleteRecent(id: Long)

    @Query("DELETE FROM recent_history")
    suspend fun clearHistory()
}
