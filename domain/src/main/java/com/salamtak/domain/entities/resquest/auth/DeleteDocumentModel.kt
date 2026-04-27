package com.salamtak.domain.entities.resquest.auth

data class DeleteDocumentModel(
    val token: String,
    val document: Document
)

data class Document(
    val DocumentId: String
)