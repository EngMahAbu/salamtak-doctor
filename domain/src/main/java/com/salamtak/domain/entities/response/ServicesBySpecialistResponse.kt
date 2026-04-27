package com.salamtak.domain.entities.response

data class ServicesBySpecialistResponse(
    val Data: List<ServicesTypesData>,
    val Message: String,
    val MessageCode: Int,
    val Success: Boolean
)

data class ServiceData(
    val Id: Int,
    val Name: String,
    val Image:String ?= null,
    var isSelected : Boolean = false
)