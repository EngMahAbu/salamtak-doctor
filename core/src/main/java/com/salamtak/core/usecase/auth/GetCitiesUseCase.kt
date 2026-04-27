package com.salamtak.core.usecase.auth

import com.salamtak.core.usecase.BaseUseCase
import com.salamtak.domain.entities.response.CitiesResponse
import com.salamtak.domain.entities.resquest.auth.QueryModel

class GetCitiesUseCase: BaseUseCase<CitiesResponse, QueryModel, String>() {
    override suspend fun execute(parameters: QueryModel, token: String): CitiesResponse {
        return webClient.getCities(token,parameters.token,parameters.QueryId)
    }
}