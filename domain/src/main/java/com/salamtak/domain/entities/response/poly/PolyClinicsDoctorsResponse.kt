package com.salamtak.domain.entities.response.poly


import com.google.gson.annotations.SerializedName

data class PolyClinicsDoctorsResponse(
    @SerializedName("Data")
    val `data`: List<ListOfDoctors>,
    @SerializedName("Message")
    val message: String,
    @SerializedName("MessageCode")
    val messageCode: Int,
    @SerializedName("Success")
    val success: Boolean
)

data class ListOfDoctors(
    @SerializedName("Birthday")
    val birthday: String,
    @SerializedName("ChangeDate")
    val changeDate: String,
    @SerializedName("CreatedByName")
    val createdByName: Any,
    @SerializedName("Email")
    val email: String,
    @SerializedName("FullName")
    val fullName: String,
    @SerializedName("FullNameAr")
    val fullNameAr: String,
    @SerializedName("GenderName")
    val genderName: String,
    @SerializedName("Id")
    val id: Int,
    @SerializedName("Image")
    val image: String,
    @SerializedName("IsSeen")
    val isSeen: Any,
    @SerializedName("LastNotification")
    val lastNotification: String,
    @SerializedName("NickName")
    val nickName: Any,
    @SerializedName("NickNameAr")
    val nickNameAr: Any,
    @SerializedName("Phone")
    val phone: String,
    @SerializedName("SeniorityLevelName")
    val seniorityLevelName: String,
    @SerializedName("SpecialistName")
    val specialistName: String,
    @SerializedName("StatusId")
    val statusId: Int,
    @SerializedName("StatusName")
    val statusName: String,
    @SerializedName("UserId")
    val userId: Int
)