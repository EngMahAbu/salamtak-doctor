package com.salamtak.domain.entities.response.data


import com.google.gson.annotations.SerializedName

data class ClinicsInfoResponse(
    @SerializedName("Data")
    val data: DataInfo,
    @SerializedName("Message")
    val message: String,
    @SerializedName("MessageCode")
    val messageCode: Int,
    @SerializedName("Success")
    val success: Boolean
)

data class DataInfo(
    @SerializedName("Address")
    val address: String,
    @SerializedName("ApartmentNo")
    val apartmentNo: String,
    @SerializedName("AreaId")
    val areaId: Int,
    @SerializedName("AreaName")
    val areaName: String,
    @SerializedName("Area_PackageID")
    val areaPackageID: Any,
    @SerializedName("BlockNo")
    val blockNo: String,
    @SerializedName("CityId")
    val cityId: Int,
    @SerializedName("CityName")
    val cityName: String,
    @SerializedName("ClinicId")
    val clinicId: Int,
    @SerializedName("CountryId")
    val countryId: Int,
    @SerializedName("Email")
    val email: String,
    @SerializedName("FixedFee")
    val fixedFee: Any,
    @SerializedName("FloorNo")
    val floorNo: Int,
    @SerializedName("HealthEntityInsID")
    val healthEntityInsID: List<Any>,
    @SerializedName("HealthEntityInsName")
    val healthEntityInsName: List<Any>,
    @SerializedName("HealthEntityPhoneDtos")
    val healthEntityPhoneDtos: List<String>,
    @SerializedName("HealthEntityServiceName")
    val healthEntityServiceName: List<Any>,
    @SerializedName("HealthEntityFeaturesNamesDtos")
    val HealthEntityFeaturesNamesDtos: List<Any>,
    @SerializedName("HealthEntityFeaturesDtos")
    val HealthEntityFeaturesDtos: List<String>,
    @SerializedName("HealthEntityServices")
    val healthEntityServices: List<String>,
    @SerializedName("Inactive")
    val inactive: Boolean,
    @SerializedName("Latitude")
    val latitude: String,
    @SerializedName("Logo")
    val logo: String,
    @SerializedName("Longitude")
    val longitude: String,
    @SerializedName("MedicalExaminationTypeId")
    val medicalExaminationTypeId: Any,
    @SerializedName("Name")
    val name: String,
    @SerializedName("NameAr")
    val nameAr: String,
    @SerializedName("Street")
    val street: Any,
    @SerializedName("SubAreaID")
    val subAreaID: Any,
    @SerializedName("SubAreaName")
    val subAreaName: Any
)