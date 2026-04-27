package com.salamtak.domain.entities.response.offers


import com.google.gson.annotations.SerializedName

data class DoctorOffersResponse(
    @SerializedName("Data")
    val `data`: List<Offers>,
    @SerializedName("Message")
    val message: String,
    @SerializedName("MessageCode")
    val messageCode: Int,
    @SerializedName("Success")
    val success: Boolean
)
data class Offers(
    @SerializedName("CS_Comment")
    val cSComment: Any,
    @SerializedName("DescAr")
    val descAr: String,
    @SerializedName("DescEn")
    val descEn: String,
    @SerializedName("DeviceNameAr")
    val deviceNameAr: String,
    @SerializedName("DeviceNameEn")
    val deviceNameEn: String,
    @SerializedName("DiscountPerc")
    val discountPerc: Int,
    @SerializedName("Doctor_Comment")
    val doctorComment: Any,
    @SerializedName("EndDate")
    val endDate: String,
    @SerializedName("HealthEntityId")
    val healthEntityId: Int,
    @SerializedName("HealthEntityName")
    val healthEntityName: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("Offer_Categoriesid")
    val offerCategoriesid: Int,
    @SerializedName("offerGalleryDtos")
    val offerGalleryDtos: List<Any>,
    @SerializedName("OfferMainCategoryID")
    val offerMainCategoryID: Int,
    @SerializedName("OfferMainCategoryName")
    val offerMainCategoryName: String,
    @SerializedName("OfferStatus")
    val offerStatus: Int,
    @SerializedName("OfferSubCategoryID")
    val offerSubCategoryID: Int,
    @SerializedName("OfferSubCategoryL1ID")
    val offerSubCategoryL1ID: Int,
    @SerializedName("OfferSubCategoryL1Name")
    val offerSubCategoryL1Name: String,
    @SerializedName("PriceAfter")
    val priceAfter: Int,
    @SerializedName("PriceBefore")
    val priceBefore: Int,
    @SerializedName("ServiceDescAr")
    val serviceDescAr: Any,
    @SerializedName("ServiceDescEn")
    val serviceDescEn: String,
    @SerializedName("ServiceNameAr")
    val serviceNameAr: String,
    @SerializedName("ServiceNameEn")
    val serviceNameEn: String,
    @SerializedName("SessionCount")
    val sessionCount: Int,
    @SerializedName("StartDate")
    val startDate: String
)