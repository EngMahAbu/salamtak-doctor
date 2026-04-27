package com.salamtak.core.usecase.auth

import com.salamtak.core.usecase.BaseUseCase
import com.salamtak.domain.entities.response.ServicesBySpecialistResponse
import com.salamtak.domain.entities.resquest.auth.QueryModel

class GetServicesBySpecialistUseCase: BaseUseCase<ServicesBySpecialistResponse,String,QueryModel>() {
    override suspend fun execute(parameters: String, token: QueryModel): ServicesBySpecialistResponse {
        return webClient.getServicesBySpecialist(parameters,token.token,token.QueryId)
    }
}