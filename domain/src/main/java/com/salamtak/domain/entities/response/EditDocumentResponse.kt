package com.salamtak.domain.entities.response

data class EditDocumentResponse(
    val Data: List<EditDocumentData>,
    val Message: String,
    val MessageCode: Int,
    val Success: Boolean
)

data class EditDocumentData(
    val DocumentUrl: String,
    val Id: Int,
    val LegalDocumentTypeId: Int,
    val LegalDocumentTypeName: String
)