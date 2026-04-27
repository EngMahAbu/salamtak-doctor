package com.salamtak.core.usecase.auth

import com.salamtak.core.usecase.BaseUseCase
import com.salamtak.domain.entities.response.CitiesResponse
import com.salamtak.domain.entities.resquest.auth.QueryModel

class GetAreasUseCase: BaseUseCase<CitiesResponse,QueryModel,String>() {
    override suspend fun execute(parameters: QueryModel, token: String): CitiesResponse {
        return webClient.getAreas(token,parameters.token,parameters.QueryId)
    }
}