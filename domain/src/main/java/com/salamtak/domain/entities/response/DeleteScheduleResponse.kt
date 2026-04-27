package com.salamtak.domain.entities.response


import com.google.gson.annotations.SerializedName

data class DeleteScheduleResponse(
    @SerializedName("Data")
    val data: DeleteSchedule,
    @SerializedName("Message")
    val message: String,
    @SerializedName("MessageCode")
    val messageCode: Int,
    @SerializedName("Success")
    val success: Boolean
)

data class DeleteSchedule(
    @SerializedName("Statues")
    val statues: Boolean
)