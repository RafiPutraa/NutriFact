package com.dicoding.nutrifact.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.nutrifact.data.ResultState
import com.dicoding.nutrifact.data.response.RedeemResponse
import com.dicoding.nutrifact.data.repository.ApiRepository
import kotlinx.coroutines.launch

class MyRedeemViewModel(private val apiRepository: ApiRepository) : ViewModel() {
    private val _redeemHistoryResult = MutableLiveData<ResultState<RedeemResponse>>()
    val redeemHistoryResult: LiveData<ResultState<RedeemResponse>> get() = _redeemHistoryResult

    fun getRedeemHistory() {
        viewModelScope.launch {
            apiRepository.getRedeemHistory().observeForever { result ->
                _redeemHistoryResult.postValue(result)
            }
        }
    }
}