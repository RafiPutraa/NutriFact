package com.dicoding.nutrifact.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.dicoding.nutrifact.data.local.entity.HistoryEntity

@Dao
interface HistoryDao {
    @Query("SELECT * FROM result_history ORDER BY id DESC")
    fun getHistory(): LiveData<List<HistoryEntity>>

    @Insert
    suspend fun insert(history: HistoryEntity)

    @Query("DELETE FROM result_history")
    suspend fun deleteAllHistory()
}