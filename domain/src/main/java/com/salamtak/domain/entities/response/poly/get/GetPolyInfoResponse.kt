package com.salamtak.domain.entities.response.poly.get


import com.google.gson.annotations.SerializedName

data class GetPolyInfoResponse(
    @SerializedName("Data")
    val `data`: DataInfo,
    @SerializedName("Message")
    val message: String,
    @SerializedName("MessageCode")
    val messageCode: Int,
    @SerializedName("Success")
    val success: Boolean
)

data class DataInfo(
    @SerializedName("CommercialRecNo")
    val commercialRecNo: String,
    @SerializedName("ID")
    val iD: Int,
    @SerializedName("Name")
    val name: String,
    @SerializedName("NameAr")
    val nameAr: Any,
    @SerializedName("OwnerID")
    val ownerID: Int,
    @SerializedName("TaxCertificateNo")
    val taxCertificateNo: String,
    @SerializedName("TaxCertificateImage1")
    val TaxCertificateImage1: String,
    @SerializedName("TaxCertificateImage2")
    val TaxCertificateImage2: String,
    @SerializedName("BioAr")
    val bioAr: String,
    @SerializedName("BioEn")
    val bioEn: String,
)