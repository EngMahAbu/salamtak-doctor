package com.salamtak.domain.entities.response

data class CreateDoctorDocumentResponse(
    val Data: CreateDoctorDocumentData ?= null,
    val Message: String,
    val MessageCode: Int,
    val Success: Boolean
)

data class CreateDoctorDocumentData(
    val Doc_Desc: Any?,
    val DocumentOrder: Int?,
    val DocumentUrl: String?,
    val DocumentUrl2: Any?,
    val Id: Int?,
    val LegalDocumentTypeId: Int?,
    val LegalDocumentTypeName: String?,
    val ProfileStatus: Int?,
    val RequiredImages: Any?,
    val URLs: Any?
)
