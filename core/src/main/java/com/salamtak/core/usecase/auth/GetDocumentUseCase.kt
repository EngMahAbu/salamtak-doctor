package com.salamtak.core.usecase.auth

import com.salamtak.core.usecase.BaseUseCase
import com.salamtak.domain.entities.response.EditDocumentResponse
import com.salamtak.domain.entities.response.LegalDocumentsData
import com.salamtak.domain.entities.response.LegalDocumentsItemsResponse

class GetDocumentUseCase : BaseUseCase<LegalDocumentsItemsResponse, String, String>() {

    override suspend fun execute(parameters: String, token: String): LegalDocumentsItemsResponse {
        return webClient.getDocument(token,parameters)
    }
}