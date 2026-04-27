package com.salamtak.domain.entities.response

data class PatientProfileResponse(
    val Data: PatientProfileData,
    val Message: String,
    val MessageCode: Int,
    val Success: Boolean
)

data class PatientProfileData(
    val AppointmentId: Int,
    val Image: String,
    val PatientAddress: String,
    val PatientAge: Int,
    val PatientBirthDate: String,
    val PatientComplain: String,
    val PatientName: String,
    val PatientPhone: String,
    val CityName: String,
    val AreaName: String,
    val Weight: Int,
    val BlockNo: String,
    val FloorNo: String,
)