package com.salamtak.domain.entities.response


import com.google.gson.annotations.SerializedName

data class GetHealthEntityResponse(
    @SerializedName("Data")
    val data: DataHealth?,
    @SerializedName("Message")
    val message: String?
)

data class DataHealth(
    @SerializedName("Items")
    val items: MutableList<ItemHealth?>,
    @SerializedName("TotalCount")
    val totalCount: Int?
)

data class ItemHealth(
    @SerializedName("Address")
    val address: String?,
    @SerializedName("ApartmentNo")
    val apartmentNo: Any?,
    @SerializedName("AreaId")
    val areaId: Int?,
    @SerializedName("AreaName")
    val areaName: String?,
    @SerializedName("BlockNo")
    val blockNo: String?,
    @SerializedName("CityId")
    val cityId: Int?,
    @SerializedName("CityName")
    val cityName: String?,
    @SerializedName("ClinicId")
    val clinicId: Int?,
    @SerializedName("CountryId")
    val countryId: Int?,
    @SerializedName("Email")
    val email: Any?,
    @SerializedName("FixedFee")
    val fixedFee: Int?,
    @SerializedName("FloorNo")
    val floorNo: Int?,
    @SerializedName("HealthEntityPhoneDtos")
    val healthEntityPhoneDtos: List<String?>?,
    @SerializedName("HealthEntityServiceName")
    val healthEntityServiceName: List<Any?>?,
    @SerializedName("HealthEntityServices")
    val healthEntityServices: List<Any?>?,
    @SerializedName("Inactive")
    val inactive: Boolean?,
    @SerializedName("Latitude")
    val latitude: Any?,
    @SerializedName("Logo")
    val logo: String?,
    @SerializedName("Longitude")
    val longitude: String?,
    @SerializedName("MedicalExaminationTypeId")
    val medicalExaminationTypeId: Any?,
    @SerializedName("Name")
    val name: String?,
    @SerializedName("NameAr")
    val nameAr: String?,
    @SerializedName("Street")
    val street: Any?
)