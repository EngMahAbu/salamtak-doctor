package com.salamtak.core.usecase.auth

import com.salamtak.core.usecase.BaseUseCase
import com.salamtak.domain.entities.response.ServicesTypesResponse

class GetServicesTypesUseCase: BaseUseCase<ServicesTypesResponse,String,String>() {
    override suspend fun execute(parameters: String, token: String): ServicesTypesResponse {
        return webClient.getServicesTypes(token,parameters)
    }
}