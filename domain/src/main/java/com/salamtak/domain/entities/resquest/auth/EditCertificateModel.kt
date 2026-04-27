package com.salamtak.domain.entities.resquest.auth

import java.io.File

data class EditCertificateModel(
    val CertificateId: String,
    val Title: String,
    val Description: String,
    val TitleAr: String,
    val DescriptionAr: String,
    val Year: String,
    val certificateImage: File?
)

data class EditCertificateModelImage(
    val CertificateId: String,
    val Title: String,
    val Description: String,
    val TitleAr: String,
    val DescriptionAr: String,
    val Year: String,
    val certificateImage: String?
)