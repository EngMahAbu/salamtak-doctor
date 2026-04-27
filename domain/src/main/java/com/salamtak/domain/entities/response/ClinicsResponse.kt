package com.salamtak.domain.entities.response

data class ClinicsResponse(
    val Data: List<ClinicData>,
    val Message: String,
    val MessageCode: Int,
    val Success: Boolean
)

data class ClinicData(
    val Address: String ? = null,
    val ClinicId: Int ?= 0,
    val Logo: String ?= null,
    val Name: String ?= null,
    val NameAr: String ?= null,
    val Phone: String ?= null,
    val AreaName: String ?= null,
    val Area_PackageID:Int?=null
)

data class UpdateClinicResponse(
    val Data: ClinicData ?= null,
    val Message: String,
    val MessageCode: Int,
    val Success: Boolean
)

data class UpdateClinicData(
    val Address: String,
    val ClinicId: Int,
    val Logo: String,
    val Name: String,
    val NameAr: String,
    val Email: String,
    val CountryId: Int,
    val CityId: Int,
    val AreaId: Int,
    val Phone: String,
//    val CityName: String,
//    val AreaName: String,
)