package com.salamtak.domain.entities.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json

data class RegisterResponse(
    @SerializedName("Data")
    @Expose
    val Data: RegisterData,
    @SerializedName("Message")
    @Expose
    val Message: String,
    @SerializedName("MessageCode")
    @Expose
    val MessageCode: Int,
    @SerializedName("Success")
    @Expose
    val Success: Boolean
)

data class RegisterData(
    @SerializedName("Code")
    @Expose
    val Code: Int,
    @SerializedName("UserId")
    @Expose
    val UserId: Int
)