package com.salamtak.domain.entities.response.hospital

import com.google.gson.JsonElement

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
    val Phones: List<String>?,
    val InfoEn: String? = null,
    val InfoAr: String? = null,
    /** Element shape not documented (ids, names or objects): read defensively */
    val Features: JsonElement? = null,
    val Services: JsonElement? = null
)
