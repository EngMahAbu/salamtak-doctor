package com.salamtak.core.usecase.auth

import com.salamtak.core.usecase.BaseUseCase
import com.salamtak.domain.entities.response.GetClinicGalleryResponse
import com.salamtak.domain.entities.resquest.auth.QueryModel

class GetClinicGalleryUseCase: BaseUseCase<GetClinicGalleryResponse,String, QueryModel>()
{
    override suspend fun execute(parameters: String, token: QueryModel): GetClinicGalleryResponse {
        return webClient.getClinicGallery(parameters,token.token,token.QueryId)
    }

}