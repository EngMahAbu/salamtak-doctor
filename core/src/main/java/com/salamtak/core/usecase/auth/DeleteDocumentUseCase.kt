package com.salamtak.core.usecase.auth

import com.salamtak.core.usecase.BaseUseCase
import com.salamtak.domain.entities.response.DefaultResponse
import com.salamtak.domain.entities.resquest.auth.DeleteDocumentModel

class DeleteDocumentUseCase: BaseUseCase<DefaultResponse,DeleteDocumentModel,String>() {
    override suspend fun execute(parameters: DeleteDocumentModel, token: String): DefaultResponse {
        return webClient.deleteDocument(token,parameters.token,parameters.document)
    }
}