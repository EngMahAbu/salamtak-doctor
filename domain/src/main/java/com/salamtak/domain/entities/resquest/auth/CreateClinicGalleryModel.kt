package com.salamtak.domain.entities.resquest.auth

import java.io.File

data class CreateClinicGalleryModel(
    val ClinicId: String,
    val clinicGallery:  File
)