package com.salamtak.domain.entities.resquest.auth

import java.io.File

data class CreateDoctorDocumentModel(
    val token: String,
    val documentModel: DocumentModel
)

data class DocumentModel(
    val LegalDocumentTypeId: Int,
    val DocumentOrder: Int,
    val document: File?
)