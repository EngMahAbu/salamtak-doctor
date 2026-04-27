package com.salamtak.core.usecase.home

import com.salamtak.core.usecase.BaseUseCase
import com.salamtak.domain.entities.response.ClinicsResponse

class GetClinicsUseCase: BaseUseCase<ClinicsResponse,String,String>()
{
    override suspend fun execute(parameters: String, token: String): ClinicsResponse {
        return webClient.getClinics(token,parameters)
    }
}