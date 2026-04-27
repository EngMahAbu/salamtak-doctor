package com.salamtak.domain.entities.response


import com.google.gson.annotations.SerializedName

data class InsuranceCompanyResponse(
    @SerializedName("Data")
    val `data`: List<InsuranceCompany>,
    @SerializedName("Message")
    val message: String,
    @SerializedName("MessageCode")
    val messageCode: Int,
    @SerializedName("Success")
    val success: Boolean
)

data class InsuranceCompany(
    @SerializedName("ID")
    val iD: String,
    @SerializedName("Id")
    val Id: String,
    @SerializedName("Name")
    val name: String
)