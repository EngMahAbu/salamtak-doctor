package com.salamtak.domain.repositories.update


import com.google.gson.annotations.SerializedName

data class UpdateDoctorProfileResponse(
    @SerializedName("Data")
    val data: Data?,
    @SerializedName("Message")
    val message: String?,
    @SerializedName("MessageCode")
    val messageCode: Int?,
    @SerializedName("Success")
    val success: Boolean?
)