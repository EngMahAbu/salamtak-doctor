package com.salamtak.domain.entities.response

data class DefaultResponse(
    val Data: Any? = "",
    val Message: String = "",
    val MessageCode: Int = 0,
    val Success: Boolean = false
)
