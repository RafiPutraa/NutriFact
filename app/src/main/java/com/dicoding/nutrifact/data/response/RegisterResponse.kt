package com.dicoding.nutrifact.data.response

import com.google.gson.annotations.SerializedName

data class RegisterResponse(
	@field:SerializedName("status")
	val status: Int? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("data")
	val data: RegisterData? = null,

)

data class RegisterData(

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("email")
	val email: String? = null,

)