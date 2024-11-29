package com.dicoding.nutrifact.data.api


import com.dicoding.nutrifact.data.response.LoginResponse
import com.dicoding.nutrifact.data.response.ProductResponse
import com.dicoding.nutrifact.data.response.ProfileResponse
import com.dicoding.nutrifact.data.response.RegisterResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String,
    ): LoginResponse

    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    @GET("products/{barcodeId}")
    suspend fun getProductByBarcode(
        @Path("barcodeId") barcodeId: String
    ): ProductResponse

    @GET("/profile/{userId}")
    fun getProfile(
        @Path("userId") userId: String?,
        @Header("Authorization") token: String?
    ): Call<ProfileResponse?>?
}