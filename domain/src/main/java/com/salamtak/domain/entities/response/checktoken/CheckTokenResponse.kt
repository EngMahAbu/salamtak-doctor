package com.salamtak.domain.entities.response.checktoken


import com.google.gson.annotations.SerializedName

data class CheckTokenResponse(
    @SerializedName("Statues")
    val statues: Boolean?
)