package com.salamtak.domain.entities.response


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ReviewsResponse(
    @SerializedName("Data")
    val data: List<ReviewsData>,
    @SerializedName("Message")
    val message: String,
    @SerializedName("MessageCode")
    val messageCode: Int,
    @SerializedName("Success")
    val success: Boolean
):Serializable

data class ReviewsData(
    @SerializedName("Comment")
    val comment: String,
    @SerializedName("PatientImage")
    val patientImage: Any,
    @SerializedName("PatientName")
    val patientName: String,
    @SerializedName("Rate")
    val rate: Int
):Serializable