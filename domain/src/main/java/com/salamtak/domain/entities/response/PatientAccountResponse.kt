package com.salamtak.domain.entities.response

data class PatientAccountResponse(
    val Data: PatientAccountData,
    val Message: String,
    val MessageCode: Int,
    val Success: Boolean
)

data class PatientAccountData(
    val Date: String,
    val DoctorId: Int,
    val DoctorName: String,
    val Id: Int,
    val Isdeleted: Boolean,
    val Paid: Int,
    val PatientId: Int,
    val Service: String,
    val ServiceFee: Int
)