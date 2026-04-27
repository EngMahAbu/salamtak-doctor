package com.salamtak.domain.entities.response.offers


import com.google.gson.annotations.SerializedName

data class OfferResponse(
    @SerializedName("Data")
    val `data`: OfferData,
    @SerializedName("Message")
    val message: String,
    @SerializedName("MessageCode")
    val messageCode: Int,
    @SerializedName("Success")
    val success: Boolean
)

data class OfferData(
    @SerializedName("offerId")
    val offerId: Int
)