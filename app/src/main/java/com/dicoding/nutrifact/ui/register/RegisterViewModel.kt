package com.dicoding.nutrifact.ui.register

import androidx.lifecycle.ViewModel
import com.dicoding.nutrifact.data.ApiRepository

class RegisterViewModel (private val apiRepository: ApiRepository) : ViewModel() {
    fun register(name: String, email: String, password: String) = apiRepository.register(name, email, password)
}