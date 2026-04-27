package com.salamtak.core.usecase.auth

import com.salamtak.core.usecase.BaseUseCase
import com.salamtak.domain.entities.response.DefaultResponse
import com.salamtak.domain.entities.resquest.auth.DeleteCertificateModel
import com.salamtak.domain.entities.resquest.auth.DeleteDocumentModel

class DeleteCertificateUseCase: BaseUseCase<DefaultResponse,DeleteCertificateModel,String>() {
    override suspend fun execute(parameters: DeleteCertificateModel, token: String): DefaultResponse {
        return webClient.deleteCertificate(token,parameters.token,parameters.certificate)
    }
}