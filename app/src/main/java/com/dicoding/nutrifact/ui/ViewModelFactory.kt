package com.dicoding.nutrifact.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.nutrifact.data.repository.ApiRepository
import com.dicoding.nutrifact.data.di.Injection
import com.dicoding.nutrifact.ui.profile.ProfileViewModel
import com.dicoding.nutrifact.viewmodel.ScanViewModel

class ViewModelFactory(private val apiRepository: ApiRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ScanViewModel::class.java) -> {
                ScanViewModel(apiRepository) as T
            }modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(apiRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(Injection.provideRepository(context))
            }.also { instance = it }
    }
}