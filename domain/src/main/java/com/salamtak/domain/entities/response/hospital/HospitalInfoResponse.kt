package com.salamtak.domain.entities.response.hospital

data class HospitalInfoResponse(
    val Data: HospitalInfoData?,
    val Message: String?,
    val MessageCode: Int?,
    val Success: Boolean?
)

data class HospitalInfoData(
    val HospitalInfoId: Int?,
    val HospitalId: Int?,
    val Name: String?,
    val NameAr: String?,
    val Logo: String?,
    val Email: String?,
    val Address: String?,
    val CityName: String?,
    val AreaName: String?,
    val Latitude: Double?,
    val Longitude: Double?,
    val Inactive: Boolean?,
    val StatusId: Int?,
    val Bio: String?,
    val Phones: List<String>?,
    val Services: List<String>?,
    val Insurance: List<String>?
)
