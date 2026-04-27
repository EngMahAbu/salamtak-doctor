package com.salamtak.domain.entities.response

data class CreateClinicGalleryResponse(
    val Data: ClinicGalleryData,
    val Message: String,
    val MessageCode: Int,
    val Success: Boolean
)

data class ClinicGalleryData(
    val Id: Int,
    val ImageUrl: String ?= null
)