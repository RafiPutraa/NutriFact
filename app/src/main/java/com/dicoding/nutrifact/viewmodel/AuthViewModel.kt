package com.dicoding.nutrifact.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.nutrifact.data.pref.UserModel
import com.dicoding.nutrifact.data.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepository : AuthRepository) : ViewModel() {
    fun login(email: String, password: String) = authRepository.login(email, password)
    fun register(name: String, email: String, password: String) = authRepository.register(name, email, password)
    fun getSession(): LiveData<UserModel> {
        return authRepository.getUser().asLiveData()
    }
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}