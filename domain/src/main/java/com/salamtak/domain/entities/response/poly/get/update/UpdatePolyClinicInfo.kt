package com.salamtak.domain.entities.response.poly.get.update


import com.google.gson.annotations.SerializedName

data class UpdatePolyClinicInfo(
    @SerializedName("Data")
    val `data`: DataDaminInfoUpdated,
    @SerializedName("Message")
    val message: String,
    @SerializedName("MessageCode")
    val messageCode: Int,
    @SerializedName("Success")
    val success: Boolean
)
data class DataDaminInfoUpdated(
    @SerializedName("Email")
    val email: String,
    @SerializedName("ID")
    val iD: Int,
    @SerializedName("IdImageBack")
    val idImageBack: Any,
    @SerializedName("IdImageFront")
    val idImageFront: String,
    @SerializedName("IdNumber")
    val idNumber: String,
    @SerializedName("Mobile1")
    val mobile1: String,
    @SerializedName("Mobile2")
    val mobile2: String,
    @SerializedName("Name")
    val name: String,
    @SerializedName("NameAr")
    val nameAr: String,
    @SerializedName("Phone")
    val phone: String,
    @SerializedName("UserID")
    val userID: Int,
    @SerializedName("UserType")
    val userType: Int
)