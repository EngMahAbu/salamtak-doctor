package com.salamtak.domain.entities.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class DoctorProfileResponse(
    @SerializedName("Data")
    @Expose
    val Data: DoctorProfileData,
    @SerializedName("Message")
    @Expose
    val Message: String,
    @SerializedName("MessageCode")
    @Expose
    val MessageCode: Int,
    @SerializedName("Success")
    @Expose
    val Success: Boolean
)

data class DoctorProfileData(
    @SerializedName("Birthday")
    @Expose
    val Birthday: String,
    @SerializedName("Code")
    @Expose
    val Code: String,
    @SerializedName("DoctorInfo")
    @Expose
    val DoctorInfo: String,
    @SerializedName("DoctorInfoAr")
    @Expose
    val DoctorInfoAr: String,
    @SerializedName("DoctorSubSpecialist")
    @Expose
    val DoctorSubSpecialist: List<Int>,
    @SerializedName("FirstName")
    @Expose
    val FirstName: String,
    @SerializedName("FirstNameAr")
    @Expose
    val FirstNameAr: String,
    @SerializedName("GenderId")
    @Expose
    val GenderId: Int,
    @SerializedName("Id")
    @Expose
    val Id: Int,
    @SerializedName("Image")
    @Expose
    val Image: String,
    @SerializedName("LastName")
    @Expose
    val LastName: String,
    @SerializedName("LastNameAr")
    @Expose
    val LastNameAr: String,
    @SerializedName("MiddelName")
    @Expose
    val MiddelName: String,
    @SerializedName("MiddelNameAr")
    @Expose
    val MiddelNameAr: String,
    @SerializedName("NationalityId")
    @Expose
    val NationalityId: Int,
    @SerializedName("ProfileStatus")
    @Expose
    val ProfileStatus: Int,
    @SerializedName("SeniorityLevelId")
    @Expose
    val SeniorityLevelId: Int,
    @SerializedName("SpecialistId")
    @Expose
    val SpecialistId: Int,
    @SerializedName("StatusId")
    @Expose
    val StatusId: Int,
    @SerializedName("UserId")
    @Expose
    val UserId: Int,
    @SerializedName("Website")
    @Expose
    val Website: String
)