package com.salamtak.domain.entities.response

data class CountriesResponse(
    val Data: List<CountriesData>,
    val Message: String,
    val MessageCode: Int,
    val Success: Boolean
)

data class CountriesData(
    val Id: Int,
    val Inactive: Boolean,
    val Name: String
)