package com.salamtak.domain.entities.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.JsonClass
import java.io.File

data class CreateCertificateResponse(
    @SerializedName("Data")
    @Expose
    val Data: CertificatesData ?= null,
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

data class CertificatesData(
    var CertificateFile : File ?= null,
    @SerializedName("CertificateUrl")
    @Expose
    var CertificateUrl: String,
    @SerializedName("Description")
    @Expose
    var Description: String,
    @SerializedName("DescriptionAr")
    @Expose
    var DescriptionAr: String,
    @SerializedName("Id")
    @Expose
    val Id: Int,
    @SerializedName("Title")
    @Expose
    var Title: String,
    @SerializedName("TitleAr")
    @Expose
    var TitleAr: String,
    @SerializedName("Year")
    @Expose
    var Year: Int
)
