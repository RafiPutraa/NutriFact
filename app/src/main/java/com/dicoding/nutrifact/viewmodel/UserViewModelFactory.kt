package com.dicoding.nutrifact.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.nutrifact.data.di.Injection
import com.dicoding.nutrifact.data.repository.AuthRepository

class UserViewModelFactory(private  val authRepository: AuthRepository) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(authRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserViewModelFactory? = null

        @JvmStatic
        fun getInstance(context: Context): UserViewModelFactory {
            if (INSTANCE == null) {
                synchronized(UserViewModelFactory::class.java) {
                    INSTANCE = UserViewModelFactory(Injection.provideAuthRepository(context))
                }
            }
            return INSTANCE as UserViewModelFactory
        }
    }
}