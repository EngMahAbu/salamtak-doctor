package com.salamtak.domain.entities.response.offers


import com.google.gson.annotations.SerializedName

data class OfferGallaryResponse(
    @SerializedName("Data")
    val `data`: Gallary,
    @SerializedName("Message")
    val message: String,
    @SerializedName("MessageCode")
    val messageCode: Int,
    @SerializedName("Success")
    val success: Boolean
)

data class Gallary(
    @SerializedName("Id")
    val id: Int,
    @SerializedName("ImageUrl")
    val imageUrl: String
)