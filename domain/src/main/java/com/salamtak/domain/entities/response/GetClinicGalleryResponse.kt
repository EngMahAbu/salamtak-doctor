package com.salamtak.domain.entities.response

data class GetClinicGalleryResponse(
    val Data: List<ClinicGalleryData>,
    val Message: String,
    val MessageCode: Int,
    val Success: Boolean
)

