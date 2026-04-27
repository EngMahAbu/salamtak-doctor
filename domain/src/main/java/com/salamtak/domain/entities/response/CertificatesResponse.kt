package com.salamtak.domain.entities.response

data class CertificatesResponse(
    val Data: List<CertificatesData>,
    val Message: String,
    val MessageCode: Int,
    val Success: Boolean
)
