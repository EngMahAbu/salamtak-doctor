package com.salamtak.domain.entities.response

data class CreateExtraInfoResponse(
    val Data: CreateExtraInfoData ?= null,
    val Message: String,
    val MessageCode: Int,
    val Success: Boolean
)

data class CreateExtraInfoData(
    val Id: Int,
    val ProfileStatus: Int
)