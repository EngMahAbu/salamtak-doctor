package com.salamtak.salamtakdoctor.salamtak_doctor.LegalDocument

import com.salamtak.domain.entities.resquest.auth.DocumentModel
import java.io.File

data class UpdateDoctorDocumentResponse(
    val token: String,
    val documentModel: UpdateDocumentModel
)

data class UpdateDocumentModel(
    val Id: Int,
    val documentUrl : File
)
