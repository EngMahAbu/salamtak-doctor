package com.salamtak.domain.entities.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SeniorityLevelsResponse(

    @SerializedName("Data")
    @Expose
    val Data: List<SeniorityLevelsData>,
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

data class SeniorityLevelsData(
    @SerializedName("Id")
    @Expose
    val Id: Int,
    @SerializedName("Inactive")
    @Expose
    val Inactive: Boolean,
    @SerializedName("Name")
    @Expose
    val Name: String
)