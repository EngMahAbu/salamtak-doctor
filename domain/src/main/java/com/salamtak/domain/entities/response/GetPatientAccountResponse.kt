package com.salamtak.domain.entities.response

data class GetPatientAccountResponse(
    val Data: List<PatientAccountData>,
    val Message: String,
    val MessageCode: Int,
    val Success: Boolean
)