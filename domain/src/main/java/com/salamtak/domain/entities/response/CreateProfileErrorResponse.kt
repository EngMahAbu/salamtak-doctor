package com.salamtak.domain.entities.response

data class CreateProfileErrorResponse(
    val Data: Any = "",
    val Message: String = "",
    val MessageCode: Int  = 0,
    val Success: Boolean = false
)