package com.salamtak.domain.entities.response.videos


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("Id")
    val id: Int?,
    @SerializedName("VideoURL")
    val videoURL: String?
)