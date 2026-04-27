package com.salamtak.domain.entities.response

data class CitiesResponse(
    val Data: List<CityData>,
    val Message: String,
    val MessageCode: Int,
    val Success: Boolean
)

data class CityData(
    val Id: Int,
    val Name: String
)