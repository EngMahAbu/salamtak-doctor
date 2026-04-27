package com.salamtak.domain.entities.resquest.auth

data class DeleteCertificateModel(
    val token: String,
    val certificate: Certificate
)

data class Certificate(
    val CertificateId : String
)