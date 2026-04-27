package com.salamtak.core.usecase.auth

import com.salamtak.core.usecase.BaseUseCase
import com.salamtak.domain.entities.response.LegalDocumentsItemsResponse

class GetLegalDocumentsItemsUseCase : BaseUseCase<LegalDocumentsItemsResponse,String,String>() {


    override suspend fun execute(parameters: String, token: String): LegalDocumentsItemsResponse {
        return webClient.getLegalDocumentsItems(token,parameters)
    }
}