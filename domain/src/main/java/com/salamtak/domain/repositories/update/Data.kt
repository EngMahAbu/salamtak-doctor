package com.salamtak.domain.repositories.update


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("Id")
    val id: Int?,
    @SerializedName("ProfileStatus")
    val profileStatus: Int?
)