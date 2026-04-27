package com.salamtak.domain.entities.response
import java.util.ArrayList
data class ServicesTypesResponse(
    val Data: ArrayList<ServicesTypesData>,
    val Message: String,
    val MessageCode: Int,
    val Success: Boolean
)

data class ServicesTypesData(
    val Id: Int,
    val Name: String,
    val Image:String,
    var isSelected : Boolean = false
)