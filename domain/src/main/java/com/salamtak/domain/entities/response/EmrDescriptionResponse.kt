package com.salamtak.domain.entities.response

data class EmrDescriptionResponse(
    val Data: EmrDescriptionData,
    val Message: String,
    val MessageCode: Int,
    val Success: Boolean
)

data class EmrDescriptionData(
    val Description: String,
    val Id: Int,
    val Title: String
)