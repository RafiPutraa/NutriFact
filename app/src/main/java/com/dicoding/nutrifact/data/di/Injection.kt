package com.dicoding.nutrifact.data.di

import com.dicoding.nutrifact.data.ApiRepository
import com.dicoding.nutrifact.data.api.ApiConfig

object Injection {
    fun provideRepository(): ApiRepository {
        val apiService = ApiConfig.getApiService()
        return ApiRepository.getInstance(apiService)
    }
}