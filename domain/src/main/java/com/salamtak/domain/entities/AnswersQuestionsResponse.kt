package com.salamtak.domain.entities


import com.google.gson.annotations.SerializedName

data class AnswersQuestionsResponse(
    @SerializedName("Data")
    val `data`: Data?,
    @SerializedName("Message")
    val message: String?,
    @SerializedName("MessageCode")
    val messageCode: Int?,
    @SerializedName("Success")
    val success: Boolean?
)
data class Data(
    @SerializedName("Statues")
    val statues: Boolean?
)