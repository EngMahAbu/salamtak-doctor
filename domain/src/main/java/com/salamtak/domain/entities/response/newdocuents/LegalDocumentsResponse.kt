package com.salamtak.domain.entities.response.newdocuents

data class LegalDocumentsResponse(
    val Data: List<LegalDataDocuments>,
    val Message: String,
    val MessageCode: Int,
    val Success: Boolean
)

data class LegalDataDocuments(
    val Doc_Desc: String,
    val DocumentOrder: Any,
    val DocumentUrl: Any,
    val DocumentUrl2: Any,
    val Id: Int,
    val ImageCount: Int,
    val IsPolyClinic: Any,
    val LegalDocumentTypeId: Int,
    val LegalDocumentTypeName: String,
    val ProfileStatus: Int,
    val RequiredImages: Int,
    val URLs: List<URL>
)
data class URL(
    val DocumentOrder: Int,
    val URL: String,
    val id: Int
)