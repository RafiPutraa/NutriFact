package com.dicoding.nutrifact.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.nutrifact.data.ResultState
import com.dicoding.nutrifact.data.repository.ApiRepository
import com.dicoding.nutrifact.data.response.ProfileResponse
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class ProfileViewModel (private val apiRepository: ApiRepository) : ViewModel() {
    private val _profileResult = MutableLiveData<ResultState<ProfileResponse>>()
    val profileResult: LiveData<ResultState<ProfileResponse>> get() = _profileResult

    private val _updateProfileResult = MutableLiveData<ResultState<ProfileResponse>>()
    val updateProfileResult: LiveData<ResultState<ProfileResponse>> get() = _updateProfileResult

    private val _currentImageUri = MutableLiveData<Uri?>()
    val currentImageUri: LiveData<Uri?> get() = _currentImageUri

    fun getProfile() {
        viewModelScope.launch {
            apiRepository.getProfile().observeForever { result ->
                _profileResult.postValue(result)
            }
        }
    }

    fun updateProfile(name: RequestBody, password: RequestBody, file: MultipartBody.Part) {
        viewModelScope.launch {
            apiRepository.updateProfile(name, password, file).observeForever { result ->
                _updateProfileResult.postValue(result)
            }
        }
    }

    fun setCurrentImageUri(uri: Uri?) {
        _currentImageUri.value = uri
    }
}