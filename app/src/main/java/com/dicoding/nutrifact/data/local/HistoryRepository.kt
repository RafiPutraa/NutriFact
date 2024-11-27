package com.dicoding.nutrifact.data.local

import androidx.lifecycle.LiveData
import com.dicoding.nutrifact.data.local.entity.HistoryEntity
import com.dicoding.nutrifact.data.local.room.HistoryDao
import com.dicoding.nutrifact.data.local.room.HistoryDatabase

class HistoryRepository(private val historyDao: HistoryDao) {
    fun getAllHistory(): LiveData<List<HistoryEntity>> {
        return historyDao.getHistory()
    }

    suspend fun insertHistory(history: HistoryEntity) {
        historyDao.insert(history)
    }

    suspend fun deleteAllHistory() {
        historyDao.deleteAllHistory()
    }

    companion object {
        @Volatile
        private var instance: HistoryRepository? = null
        fun getInstance(database: HistoryDatabase): HistoryRepository {
            return instance ?: synchronized(this) {
                instance ?: HistoryRepository(database.historyDao()).also { instance = it }
            }
        }
    }
}