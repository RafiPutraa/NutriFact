package com.dicoding.nutrifact.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.nutrifact.data.ResultState
import com.dicoding.nutrifact.data.repository.ApiRepository
import com.dicoding.nutrifact.data.response.ProfileResponse
import kotlinx.coroutines.launch

class ProfileViewModel (private val apiRepository: ApiRepository) : ViewModel() {
    private val _profileResult = MutableLiveData<ResultState<ProfileResponse>>()
    val profileResult: LiveData<ResultState<ProfileResponse>> get() = _profileResult

    fun getProfile() {
        viewModelScope.launch {
            apiRepository.getProfile().observeForever { result ->
                _profileResult.postValue(result)
            }
        }
    }
}