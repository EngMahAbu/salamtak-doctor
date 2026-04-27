package com.salamtak.domain.entities.response

data class WhatsUpAdsResponse(
    val Data: List<AdsData>,
    val Message: String,
    val MessageCode: Int,
    val Success: Boolean
)

data class AdsData(
    val AdOrder: Int,
    val CategoryID: Any,
    val CompName: String,
    val Deleted: Boolean,
    val Discription: String,
    val Id: Int,
    val IsDefault: Boolean,
    val IsDoctor: Boolean,
    val IsVideo: Boolean,
    val PageID: Int,
    val URL: Any,
    val VideoLink: String,
    val getAd_ImagesDtos: List<GetAdImagesDto>
)

data class GetAdImagesDto(
    val AdID: Int,
    val Deleted: Boolean,
    val Id: Int,
    val ImageOrder: Int,
    val ImageURL: String
)

