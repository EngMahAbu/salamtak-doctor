package com.salamtak.domain.entities.response

data class ResetPasswordResponse(
    val Data: ResetPasswordData,
    val Message: String,
    val MessageCode: Int,
    val Success: Boolean
)

data class ResetPasswordData(
    val Code: Int,
    val ReSendCounter: Int,
    val UserId: Int
)