package com.salamtak.domain.entities.response

data class HomeAdsResponse(
    val Data: List<HomeAdsData>,
    val Message: String,
    val MessageCode: Int,
    val Success: Boolean
)

data class HomeAdsData(
    val IsVideo: Boolean,
    val VideoLink: String,
    val Id:Int=0
)