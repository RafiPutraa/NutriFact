package com.dicoding.nutrifact.data.repository

import androidx.lifecycle.liveData
import com.dicoding.nutrifact.data.ResultState
import com.dicoding.nutrifact.data.api.ApiService
import com.dicoding.nutrifact.data.pref.UserModel
import com.dicoding.nutrifact.data.pref.UserPreference
import com.dicoding.nutrifact.data.response.LoginResponse
import com.dicoding.nutrifact.data.response.RegisterResponse
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

class AuthRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {
    fun login(email: String, password: String) = liveData {
        emit(ResultState.Loading)
        try {
            val loginResponse = apiService.login(email, password)

            val token: String? = loginResponse.data?.token
            val userId: String? = loginResponse.data?.user?.id
            val name: String? = loginResponse.data?.user?.name
            val emailRes: String? = loginResponse.data?.user?.email
            if (loginResponse.data != null && userId != null && name != null && token != null && emailRes != null) {
                val user = UserModel(
                    token,
                    userId,
                    name,
                    emailRes
                )
                saveUser(user)
                emit(ResultState.Success(loginResponse))
            }
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
            emit(
                ResultState.Error(
                    errorResponse.message ?: "An error occurred during registration"
                )
            )
        } catch (e: SocketTimeoutException) {
            emit(ResultState.Error("The server is not responding. Please try again later."))
        } catch (e: IOException) {
            emit(ResultState.Error("Unable to connect to the server. Check your internet connection."))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message ?: "An unexpected error occurred."))
        }
    }

    private suspend fun saveUser(user: UserModel) {
        userPreference.saveUser(user)
    }

    fun getUser(): Flow<UserModel> {
        return userPreference.getUser()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        @Volatile
        private var instance: AuthRepository? = null
        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference
        ): AuthRepository =
            instance ?: synchronized(this) {
                instance ?: AuthRepository(apiService,userPreference)
            }.also { instance = it }
    }
}