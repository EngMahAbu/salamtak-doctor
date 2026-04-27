package com.salamtak.domain.entities.response.poly


import com.google.gson.annotations.SerializedName

data class PolyClinicAdminResponse(
    @SerializedName("Data")
    val `data`: PolyData,
    @SerializedName("Message")
    val message: String,
    @SerializedName("MessageCode")
    val messageCode: Int,
    @SerializedName("Success")
    val success: Boolean
)

data class PolyData(
    @SerializedName("OwnerID")
    val ownerID: Int
)