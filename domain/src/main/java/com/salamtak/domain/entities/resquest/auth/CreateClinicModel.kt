package com.salamtak.domain.entities.resquest.auth

import java.io.File

data class CreateClinicModel(
    val HealthEntityPhoneDtos: List<String>,
    val HealthEntityServiceDtos: List<String>,
    val insurance_CompaniesID: List<String>,
    val featuresId: List<String>,
    val Name: String,
    val NameAr: String,
    var Email: String? = null,
    val CountryId: String,
    val CityId: String,
    val AreaId: String,
    val Address: String,
    val Latitude: String,
    val Longitude: String,
    val BlockNo: String,
    val FloorNo: String,
    val FixedFee: String,
    val ApartmentNo: String,
    val Inactive: Boolean,
    val Street: String,
    var clinicLogo: File? = null,
    var ClinicId: String? = null

)