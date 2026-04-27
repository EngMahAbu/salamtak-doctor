package com.salamtak.domain.entities.response.offers


import com.google.gson.annotations.SerializedName

data class SubCategoryResponse(
    @SerializedName("Data")
    val data: List<SubcategoryData>,
    @SerializedName("Message")
    val message: String,
    @SerializedName("MessageCode")
    val messageCode: Int,
    @SerializedName("Success")
    val success: Boolean
)

data class SubcategoryData(
    @SerializedName("id")
    val id: Int,
    @SerializedName("Name")
    val name: String
)