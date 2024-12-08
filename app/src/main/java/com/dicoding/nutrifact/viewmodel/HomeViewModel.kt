package com.dicoding.nutrifact.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.nutrifact.data.ResultState
import com.dicoding.nutrifact.data.repository.ApiRepository
import com.dicoding.nutrifact.data.response.PointResponse
import com.dicoding.nutrifact.data.response.ProfileResponse
import kotlinx.coroutines.launch

class HomeViewModel(private val apiRepository: ApiRepository) : ViewModel() {
    private val _profileResult = MutableLiveData<ResultState<ProfileResponse>>()
    val profileResult: LiveData<ResultState<ProfileResponse>> get() = _profileResult

    private val _awardResult = MutableLiveData<ResultState<PointResponse>>()
    val awardResult: LiveData<ResultState<PointResponse>> get() = _awardResult

    private val _redeemAwardResult = MutableLiveData<ResultState<PointResponse>>()
    val redeemAwardResult: LiveData<ResultState<PointResponse>> get() = _redeemAwardResult

    fun getProfile() {
        viewModelScope.launch {
            apiRepository.getProfile().observeForever { result ->
                _profileResult.postValue(result)
            }
        }
    }

    fun getAward() {
        viewModelScope.launch {
            apiRepository.getAward().observeForever { result ->
                _awardResult.postValue(result)
            }
        }
    }

    fun redeemAward(awardId: String) {
        viewModelScope.launch {
            apiRepository.redeemAward(awardId).observeForever { result ->
                _redeemAwardResult.postValue(result)
            }
        }
    }
}
