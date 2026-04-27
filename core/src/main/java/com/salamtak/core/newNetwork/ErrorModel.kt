package com.salamtak.core.newNetwork

data class ErrorModel(
    val Data: Any,
    val Message: String?,
    val MessageCode: Int,
    val Success: Boolean,
    var code :Int
)