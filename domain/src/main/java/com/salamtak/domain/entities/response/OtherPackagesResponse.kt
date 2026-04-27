package com.salamtak.domain.entities.response


import com.google.gson.annotations.SerializedName

data class OtherPackagesResponse(
    @SerializedName("Data")
    val data: List<OtherPackagesData>,
    @SerializedName("Message")
    val message: String,
    @SerializedName("MessageCode")
    val messageCode: Int,
    @SerializedName("Success")
    val success: Boolean
)

data class OtherPackagesData(
    @SerializedName("access_to_sponsored_ads")
    val accessToSponsoredAds: Boolean,
    @SerializedName("AreaNameAr")
    val areaNameAr: String,
    @SerializedName("AreaNameEn")
    val areaNameEn: String,
    @SerializedName("cashback_on_repeat_patient")
    val cashbackOnRepeatPatient: Int,
    @SerializedName("CityNameAr")
    val cityNameAr: String,
    @SerializedName("CityNameEN")
    val cityNameEN: String,
    @SerializedName("Extra_inclinic_insurance_Price")
    val extraInclinicInsurancePrice: Int,
    @SerializedName("Extra_inclinic_patientPrice")
    val extraInclinicPatientPrice: Int,
    @SerializedName("Extra_teleconsultationPrice")
    val extraTeleconsultationPrice: Int,
    @SerializedName("inclinic_insurance_Petients")
    val inclinicInsurancePetients: Int,
    @SerializedName("inclinic_patients")
    val inclinicPatients: Int,
    @SerializedName("One_month_free_sponsored_Ads")
    val oneMonthFreeSponsoredAds: Boolean,
    @SerializedName("PackageID")
    val packageID: Int,
    @SerializedName("PackageName")
    val packageName: String,
    @SerializedName("PackagePrice")
    val packagePrice: Int,
    @SerializedName("SpecNameAr")
    val specNameAr: String,
    @SerializedName("SpecNameEn")
    val specNameEn: String,
    @SerializedName("tele_consultations")
    val teleConsultations: Int
)