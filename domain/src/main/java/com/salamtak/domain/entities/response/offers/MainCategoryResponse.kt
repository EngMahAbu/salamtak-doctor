package com.salamtak.domain.entities.response.offers


import com.google.gson.annotations.SerializedName

data class MainCategoryResponse(
    @SerializedName("Data")
    val category: List<Category>,
    @SerializedName("Message")
    val message: String,
    @SerializedName("MessageCode")
    val messageCode: Int,
    @SerializedName("Success")
    val success: Boolean
)

data class Category(
    @SerializedName("id")
    val id: Int,
    @SerializedName("image")
    val image: String,
    @SerializedName("Name")
    val name: String
)