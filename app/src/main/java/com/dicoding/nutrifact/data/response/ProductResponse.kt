package com.dicoding.nutrifact.data.response

import com.google.gson.annotations.SerializedName

data class ProductResponse(

	@field:SerializedName("data")
	val data: ProductData? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class ProductData(

	@field:SerializedName("merk")
	val merk: String? = null,

	@field:SerializedName("varian")
	val varian: String? = null,

	@field:SerializedName("fat")
	val fat: String? = null,

	@field:SerializedName("healthGrade")
	val healthGrade: String? = null,

	@field:SerializedName("sugar")
	val sugar: String? = null,

	@field:SerializedName("barcodeId")
	val barcodeId: String? = null
)
