package com.salamtak.domain.entities.response.hospital

data class HospitalClinicsResponse(
    val Data: List<HospitalClinic>?,
    val Message: String?,
    val MessageCode: Int?,
    val Success: Boolean?
)

data class HospitalClinic(
    val ClinicId: Int,
    val Name: String?,
    val NameAr: String?,
    val Email: String?,
    val Logo: String?,
    val FixedFee: Double?,
    val Hospital_InfoID: Int?,
    val FollowUp_Period: Int?,
    val Inactive: Boolean?,
    val Phones: List<String>?
)
