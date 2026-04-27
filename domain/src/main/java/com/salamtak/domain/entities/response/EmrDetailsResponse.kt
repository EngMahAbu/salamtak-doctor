package com.salamtak.domain.entities.response

data class EmrDetailsResponse(
    val Data: EmrDetailsData,
    val Message: String,
    val MessageCode: Int,
    val Success: Boolean
)

data class EmrDetailsData(
    val AppointmentId: Int,
    val Instructions: String,
    val PatientEmrdetails: List<PatientEmrdetail>,
    val PatientEmrdocuments: List<Any>
)

data class PatientEmrdetail(
    val Description: String,
    val Id: Int,
    val Title: String
)