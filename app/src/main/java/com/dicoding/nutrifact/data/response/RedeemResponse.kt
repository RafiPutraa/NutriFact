package com.dicoding.nutrifact.data.response

import com.google.gson.annotations.SerializedName

data class RedeemResponse (
    @field:SerializedName("status")
    val status: String? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("data")
    val data: List<RedeemData>? = null,
)

data class RedeemData(
    @field:SerializedName("userId")
    val userId: String? = null,

    @field:SerializedName("awardId")
    val awardId: String? = null,

    @field:SerializedName("awardName")
    val awardName: String? = null,

    @field:SerializedName("pointsRedeemed")
    val pointsRedeemed: String? = null,

    @field:SerializedName("redeemedAt")
    val redeemedAt: String? = null,

    @field:SerializedName("imageURL")
    val imageURL: String? = null,
)