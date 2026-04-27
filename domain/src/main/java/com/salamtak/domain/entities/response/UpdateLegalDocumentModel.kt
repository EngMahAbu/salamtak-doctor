package com.salamtak.domain.entities.response

data class UpdateLegalDocumentModel(
    val Data: DataLegalDocument,
    val Message: String,
    val MessageCode: Int,
    val Success: Boolean
)

data class DataLegalDocument(
    val Doc_Desc: Any?,
    val DocumentUrl: String ?= null,
    val DocumentUrl2: Any?,
    val Id: Int,
    val LegalDocumentTypeId: Int,
    val LegalDocumentTypeName: String ?= null,
    val ProfileStatus: Int,
    val RequiredImages: Any?,
    val URLs: Any?
)