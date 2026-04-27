package com.salamtak.domain.entities.response

data class VactionResponse(
    val Data: List<VactionData?>,
    val Message: String,
    val MessageCode: Int,
    val Success: Boolean
)

data class VactionData(
    val ClinicID: Int,
    val DoctorID: Int,
    val FromDate: String,
    val ToDate: String,
    val VacationName: String,
    val id: Int,
    val ClinicName:String,
    val AreaName:String,
    val CreateDate:String
)