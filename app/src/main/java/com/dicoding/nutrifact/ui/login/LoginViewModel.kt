package com.dicoding.nutrifact.ui.login

import androidx.lifecycle.ViewModel
import com.dicoding.nutrifact.data.ApiRepository

class LoginViewModel (private val apiRepository: ApiRepository) : ViewModel() {
    fun login(email: String, password: String) = apiRepository.login(email, password)
}