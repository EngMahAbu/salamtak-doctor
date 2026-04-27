package com.salamtak.domain.entities.response.poly.get


import com.google.gson.annotations.SerializedName

data class GetPolyResponse(
    @SerializedName("Data")
    val `data`: DataGetPoly,
    @SerializedName("Message")
    val message: String,
    @SerializedName("MessageCode")
    val messageCode: Int,
    @SerializedName("Success")
    val success: Boolean
)

data class DataGetPoly(
    @SerializedName("Email")
    val email: String,
    @SerializedName("ID")
    val iD: Int,
    @SerializedName("IdNumber")
    val idNumber: String,
    @SerializedName("Mobile1")
    val mobile1: Any,
    @SerializedName("Mobile2")
    val mobile2: Any,
    @SerializedName("Name")
    val name: String,
    @SerializedName("NameAr")
    val nameAr: String,
    @SerializedName("Phone")
    val phone: String,
    @SerializedName("UserID")
    val userID: Int,
    @SerializedName("UserType")
    val userType: Int,
    @SerializedName("IdImageFront")
    val IdImageFront: String,
    @SerializedName("IdImageBack")
    val IdImageBack: String,
)