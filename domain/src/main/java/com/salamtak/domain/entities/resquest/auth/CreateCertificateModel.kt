package com.salamtak.domain.entities.resquest.auth

import java.io.File

data class CreateCertificateModel(
    val Title: String,
    val Description: String,
    val TitleAr: String,
    val DescriptionAr: String,
    val Year: Int,
    val certificateImage: File
)