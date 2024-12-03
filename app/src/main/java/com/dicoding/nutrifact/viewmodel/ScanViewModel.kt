package com.dicoding.nutrifact.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.nutrifact.data.repository.ApiRepository
import com.dicoding.nutrifact.data.ResultState
import com.dicoding.nutrifact.data.response.ProductResponse
import kotlinx.coroutines.launch

class ScanViewModel (private val apiRepository: ApiRepository) : ViewModel() {
    private val _currentImageUri = MutableLiveData<Uri?>()
    val currentImageUri: LiveData<Uri?> get() = _currentImageUri

    private val _productResponse = MutableLiveData<ResultState<ProductResponse>>()
    val productResponse: LiveData<ResultState<ProductResponse>> get() = _productResponse

    fun setCurrentImageUri(uri: Uri?) {
        _currentImageUri.value = uri
    }

    fun getProductByBarcode(barcodeId: String) {
        viewModelScope.launch {
            apiRepository.getProductByBarcode(barcodeId).observeForever { result ->
                _productResponse.value = result
            }
        }
    }
}