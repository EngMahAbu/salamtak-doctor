package com.salamtak.domain.entities.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class UserLoginResponse(
    @SerializedName("Data")
    @Expose
    val Data: Data,
    @SerializedName("Message")
    @Expose
    val Message: String,
    @SerializedName("MessageCode")
    @Expose
    val MessageCode: Int,
    @SerializedName("Success")
    @Expose
    val Success: Boolean
)

data class Data(
    @SerializedName("Id")
    @Expose
    val Id: Int,
    @SerializedName("Phone")
    @Expose
    val Phone: String,
    @SerializedName("Token")
    @Expose
    val Token: String,
    @SerializedName("Name")
    @Expose
    val Name: String,
    @SerializedName("NameAR")
    @Expose
    val NameAR: String?,
    @SerializedName("Image")
    @Expose
    val Image: String?,
    @SerializedName("ProfileStatus")
    @Expose
    val ProfileStatus: Int,
    @SerializedName("Email")
    @Expose
    val Email: String,
    val OwnerID: Int,
    val InfoID: Int,
)