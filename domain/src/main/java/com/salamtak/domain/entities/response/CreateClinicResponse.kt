package com.salamtak.domain.entities.response

data class CreateClinicResponse(
    val Data: CreateClinicData ?= null,
    val Message: String,
    val MessageCode: Int,
    val Success: Boolean
)

data class CreateClinicData(
    val ClinicId: Int
)