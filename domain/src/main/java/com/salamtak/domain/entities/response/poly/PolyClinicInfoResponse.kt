package com.salamtak.domain.entities.response.poly


import com.google.gson.annotations.SerializedName

data class PolyClinicInfoResponse(
    @SerializedName("Data")
    val `data`: PolyClinicInfoData,
    @SerializedName("Message")
    val message: String,
    @SerializedName("MessageCode")
    val messageCode: Int,
    @SerializedName("Success")
    val success: Boolean
)

data class PolyClinicInfoData(
    @SerializedName("ID")
    val iD: Int
)