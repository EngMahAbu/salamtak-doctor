package com.salamtak.domain.entities.response.emr


import com.google.gson.annotations.SerializedName

data class EmrResponse(
    @SerializedName("Data")
    val `data`: List<Data?>?,
    @SerializedName("Message")
    val message: String?,
    @SerializedName("MessageCode")
    val messageCode: Int?,
    @SerializedName("Success")
    val success: Boolean?
)