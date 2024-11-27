package com.dicoding.nutrifact.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dicoding.nutrifact.data.local.entity.HistoryEntity

@Database(entities = [HistoryEntity::class], version = 1, exportSchema = false)
abstract class HistoryDatabase: RoomDatabase(){
    abstract fun historyDao(): HistoryDao

    companion object{
        @Volatile
        private var INSTANCE: HistoryDatabase? = null

        @JvmStatic
        private var instance:HistoryDatabase? = null
        fun getInstance(context: Context):HistoryDatabase =
            instance ?: synchronized(this){
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    HistoryDatabase::class.java, "app_history.db"
                ).build()
            }
    }
}