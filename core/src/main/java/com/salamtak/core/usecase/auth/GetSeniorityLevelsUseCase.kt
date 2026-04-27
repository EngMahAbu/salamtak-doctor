package com.salamtak.core.usecase.auth

import com.salamtak.core.usecase.BaseUseCase
import com.salamtak.domain.entities.response.SeniorityLevelsResponse

class GetSeniorityLevelsUseCase: BaseUseCase<SeniorityLevelsResponse,String,String>() {


    override suspend fun execute(parameters: String,token: String): SeniorityLevelsResponse {
        return webClient.getSeniorityLevels(token,parameters)
    }


}