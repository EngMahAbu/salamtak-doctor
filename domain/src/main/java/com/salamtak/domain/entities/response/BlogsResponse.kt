package com.salamtak.domain.entities.response


import com.google.gson.annotations.SerializedName

data class BlogsResponse(
    @SerializedName("Data")
    val data: List<Blogs>,
    @SerializedName("Message")
    val message: String,
    @SerializedName("MessageCode")
    val messageCode: Int,
    @SerializedName("Success")
    val success: Boolean
)

data class Blogs(
    @SerializedName("CreatedDate")
    val createdDate: String?,
    @SerializedName("Description")
    val description: String?,
    @SerializedName("id")
    val id: Int,
    @SerializedName("image")
    val image: String?,
    @SerializedName("inactive")
    val inactive: Boolean,
    @SerializedName("isVideo")
    val isVideo: Boolean,
    @SerializedName("Title")
    val title: String?,
    @SerializedName("TypeID")
    val typeID: Int?,
    @SerializedName("Links")
    val links: List<String>?,
)