package com.salamtak.domain.entities.response

data class ClinicInfoResponse(
    val Data: ClinicInfoData,
    val Message: String,
    val MessageCode: Int,
    val Success: Boolean
)

data class ClinicInfoData(
    val Address: String,
    val AreaId: Int,
    val AreaName: String,
    val BlockNo: Int,
    val CityId: Int,
    val CityName: String,
    val ClinicId: Int,
    val CountryId: Int,
    val Email: String,
    val FixedFee: Int,
    val FloorNo: Int,
    val HealthEntityPhoneDtos: List<String>,
    val HealthEntityServiceName: List<String>,
    val HealthEntityServices: List<Int>,
    val Inactive: Boolean,
    val Latitude: Any,
    val Logo: String,
    val Longitude: Any,
    val Name: String,
    val NameAr: String,
    val ApartmentNo: String,
    val Street: String
)


//    (
//    val Data: ClinicInfoData,
//    val Message: String,
//    val MessageCode: Int,
//    val Success: Boolean
//)
//
//data class ClinicInfoData(
//    val Address: String,
//    val AreaId: Int,
//    val BlockNo: Int,
//    val CityId: Int,
//    val ClinicId: Int,
//    val CountryId: Int,
//    val Email: String,
//    val FixedFee: Int,
//    val FloorNo: Int,
//    val HealthEntityPhoneDtos: List<String>,
//    val Inactive: Boolean,
//    val Latitude: Any,
//    val Logo: String,
//    val Longitude: Any,
//    val Name: String,
//    val NameAr: String
//)