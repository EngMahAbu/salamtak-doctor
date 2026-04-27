package com.salamtak.domain.entities.response


import com.google.gson.annotations.SerializedName

data class DeleteSchduleRequest(
    @SerializedName("IsApproved")
    val isApproved: Boolean?=null,
    @SerializedName("SchedualId")
    val schedualId: Int
)