package com.salamtak.core.usecase.auth

import com.salamtak.core.usecase.BaseUseCase
import com.salamtak.domain.entities.response.CreateDoctorDocumentResponse
import com.salamtak.domain.entities.resquest.auth.CreateDoctorDocumentModel

class CreateDoctorDocumentUseCase: BaseUseCase<CreateDoctorDocumentResponse,CreateDoctorDocumentModel,String>()
{
    override suspend fun execute(parameters: CreateDoctorDocumentModel, token: String): CreateDoctorDocumentResponse {
        return webClient.createDoctorDocument(token,parameters.token,parameters.documentModel)
    }


}