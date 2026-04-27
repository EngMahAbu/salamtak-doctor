package com.salamtak.domain.entities.response


import com.google.gson.annotations.SerializedName

data class SalamtakGateResponse(
    @SerializedName("Data")
    val data: List<Gates>,
    @SerializedName("Message")
    val message: String,
    @SerializedName("MessageCode")
    val messageCode: Int,
    @SerializedName("Success")
    val success: Boolean
)


data class Gates(
    @SerializedName("id")
    val id: Int,
    @SerializedName("ImageURL")
    val imageURL: String
)