package com.salamtak.domain.entities.response.offers


import com.google.gson.annotations.SerializedName

data class GetGallaryByIDResponse(
    @SerializedName("Data")
    val `data`: List<AllGAllary>,
    @SerializedName("Message")
    val message: String,
    @SerializedName("MessageCode")
    val messageCode: Int,
    @SerializedName("Success")
    val success: Boolean
)
data class AllGAllary(
    @SerializedName("Id")
    val id: Int,
    @SerializedName("ImageUrl")
    val imageUrl: String
)