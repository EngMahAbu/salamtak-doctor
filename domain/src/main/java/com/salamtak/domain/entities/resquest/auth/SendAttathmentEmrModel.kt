package com.salamtak.domain.entities.resquest.auth

import java.io.File

data class SendAttathmentEmrModel(
    val AppointmentId: String,
    val document: File
)