package com.dicoding.nutrifact.data.repository

import androidx.lifecycle.liveData
import com.dicoding.nutrifact.data.ResultState
import com.dicoding.nutrifact.data.api.ApiService
import com.dicoding.nutrifact.data.response.LoginResponse
import com.dicoding.nutrifact.data.response.ProductResponse
import com.dicoding.nutrifact.data.response.RegisterResponse
import com.google.gson.Gson
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

class ApiRepository private constructor(
    private val apiService: ApiService
) {
    fun getProductByBarcode(barcodeId: String) = liveData {
        emit(ResultState.Loading)
        try {
            val productResponse = apiService.getProductByBarcode(barcodeId)
            emit(ResultState.Success(productResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ProductResponse::class.java)
            emit(ResultState.Error(errorResponse.message ?: "Product not found"))
        } catch (e: SocketTimeoutException) {
            emit(ResultState.Error("The server is not responding. Please try again later."))
        } catch (e: IOException) {
            emit(ResultState.Error("Unable to connect to the server. Check your internet connection."))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message ?: "An unexpected error occurred."))
        }
    }

    fun getProfile() = liveData {
        emit(ResultState.Loading)
        try {
            val profileResponse = apiService.getProfile()
            emit(ResultState.Success(profileResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            emit(ResultState.Error(errorBody ?: "Failed to fetch profile data."))
        } catch (e: SocketTimeoutException) {
            emit(ResultState.Error("The server is not responding. Please try again later."))
        } catch (e: IOException) {
            emit(ResultState.Error("Unable to connect to the server. Check your internet connection."))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message ?: "An unexpected error occurred."))
        }
    }

    fun updateProfile(name: RequestBody, password: RequestBody, file: MultipartBody.Part) = liveData {
        emit(ResultState.Loading)
        try {
            val profileResponse = apiService.updateProfile(name, password, file)
            emit(ResultState.Success(profileResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            emit(ResultState.Error(errorBody ?: "Failed to update profile."))
        } catch (e: SocketTimeoutException) {
            emit(ResultState.Error("The server is not responding. Please try again later."))
        } catch (e: IOException) {
            emit(ResultState.Error("Unable to connect to the server. Check your internet connection."))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message ?: "An unexpected error occurred."))
        }
    }


    companion object {
        @Volatile
        private var instance: ApiRepository? = null

        fun getInstance(apiService: ApiService) =
            instance ?: synchronized(this) {
                instance ?: ApiRepository(apiService)
            }.also { instance = it }
    }
}