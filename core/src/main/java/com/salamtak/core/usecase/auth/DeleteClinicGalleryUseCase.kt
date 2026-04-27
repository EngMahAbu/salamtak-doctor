package com.salamtak.core.usecase.auth

import com.salamtak.core.usecase.BaseUseCase
import com.salamtak.domain.entities.response.DefaultResponse
import com.salamtak.domain.entities.response.GetClinicGalleryResponse
import com.salamtak.domain.entities.resquest.auth.QueryModel

class DeleteClinicGalleryUseCase: BaseUseCase<DefaultResponse,String, QueryModel>()
{
    override suspend fun execute(parameters: String, token: QueryModel): DefaultResponse {
        return webClient.deleteClinicGallery(parameters,token.token,token.QueryId)
    }

}