package com.dicoding.nutrifact.data.response

import com.google.gson.annotations.SerializedName

data class PointResponse(
    @field:SerializedName("status")
    val status: Int? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("data")
    val data: List<PointData>? = null,
)

data class PointData(

    @field:SerializedName("awardId")
    val awardId: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("imageURL")
    val imageURL: String? = null,

    @field:SerializedName("pointsRequired")
    val pointsRequired: String? = null,
)