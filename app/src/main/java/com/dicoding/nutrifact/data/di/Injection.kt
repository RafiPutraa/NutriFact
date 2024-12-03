package com.dicoding.nutrifact.data.di

import android.content.Context
import com.dicoding.nutrifact.data.repository.ApiRepository
import com.dicoding.nutrifact.data.api.ApiConfig
import com.dicoding.nutrifact.data.pref.UserPreference
import com.dicoding.nutrifact.data.pref.dataStore
import com.dicoding.nutrifact.data.repository.AuthRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): ApiRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getUser().first() }
        val apiService = ApiConfig.getApiService(pref)
        return ApiRepository.getInstance(apiService)
    }

    fun provideAuthRepository(context : Context) : AuthRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService(pref)

        return AuthRepository.getInstance(apiService,pref)
    }
}