package com.dicoding.nutrifact.data

import androidx.lifecycle.liveData
import com.dicoding.nutrifact.data.api.ApiService
import com.dicoding.nutrifact.data.response.LoginResponse
import com.dicoding.nutrifact.data.response.ProductResponse
import com.dicoding.nutrifact.data.response.RegisterResponse
import com.google.gson.Gson
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

class ApiRepository private constructor(
    private val apiService: ApiService
) {
    fun login(email: String, password: String) = liveData {
        emit(ResultState.Loading)
        try {
            val loginResponse = apiService.login(email, password)
            emit(ResultState.Success(loginResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, LoginResponse::class.java)
            emit(ResultState.Error(errorResponse.message ?: "An error occurred during login"))
        } catch (e: SocketTimeoutException) {
            emit(ResultState.Error("The server is not responding. Please try again later."))
        } catch (e: IOException) {
            emit(ResultState.Error("Unable to connect to the server. Check your internet connection."))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message ?: "An unexpected error occurred."))
        }
    }

    fun register(name: String, email: String, password: String) = liveData {
        emit(ResultState.Loading)
        try {
            val registerResponse = apiService.register(name, email, password)
            emit(ResultState.Success(registerResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, RegisterResponse::class.java)
            emit(ResultState.Error(errorResponse.message ?: "An error occurred during registration"))
        } catch (e: SocketTimeoutException) {
            emit(ResultState.Error("The server is not responding. Please try again later."))
        } catch (e: IOException) {
            emit(ResultState.Error("Unable to connect to the server. Check your internet connection."))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message ?: "An unexpected error occurred."))
        }
    }

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

    companion object {
        @Volatile
        private var instance: ApiRepository? = null

        fun getInstance(apiService: ApiService) =
            instance ?: synchronized(this) {
                instance ?: ApiRepository(apiService)
            }.also { instance = it }
    }
}