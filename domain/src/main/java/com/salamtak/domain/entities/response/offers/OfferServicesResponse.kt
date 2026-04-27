package com.salamtak.domain.entities.response.offers


import com.google.gson.annotations.SerializedName

data class OfferServicesResponse(
    @SerializedName("Data")
    val data: List<ServicesData>,
    @SerializedName("Message")
    val message: String,
    @SerializedName("MessageCode")
    val messageCode: Int,
    @SerializedName("Success")
    val success: Boolean
)

data class ServicesData(
    @SerializedName("id")
    val id: Int,
    @SerializedName("Name")
    val name: String
)