package com.salamtak.domain.entities.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CreateDoctorProfileResponse(
    @SerializedName("Data")
    @Expose
    val Data: CreateDoctorProfileData = CreateDoctorProfileData(),
    @SerializedName("Message")
    @Expose
    val Message: String = "",
    @SerializedName("MessageCode")
    @Expose
    val MessageCode: Int = 0,
    @SerializedName("Success")
    @Expose
    val Success: Boolean = false
)

data class CreateDoctorProfileData(
    @SerializedName("Id")
    @Expose
    val Id: Int = 0,
    @SerializedName("ProfileStatus")
    @Expose
    val ProfileStatus: Int = 0
)
