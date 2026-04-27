package com.salamtak.domain.entities.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CreateSubSpecialistResponse(
    @SerializedName("Data")
    @Expose
    val Data: CreateSubSpecialistData,
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

data class CreateSubSpecialistData(
    @SerializedName("Id")
    @Expose
    val Id: Int,
    @SerializedName("Inactive")
    @Expose
    val Inactive: Boolean,
    @SerializedName("Name")
    @Expose
    val Name: String,
    @SerializedName("ParentId")
    @Expose
    val ParentId: Int,
    @SerializedName("SpecialistParentName")
    @Expose
    val SpecialistParentName: Int
)