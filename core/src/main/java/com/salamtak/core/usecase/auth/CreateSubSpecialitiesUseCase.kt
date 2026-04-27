package com.salamtak.core.usecase.auth

import com.salamtak.core.usecase.BaseUseCase
import com.salamtak.domain.entities.response.CreateSubSpecialistResponse
import com.salamtak.domain.entities.response.SubSpecialitiesResponse
import com.salamtak.domain.entities.resquest.auth.CreateSpecialistModel

class CreateSubSpecialitiesUseCase: BaseUseCase<CreateSubSpecialistResponse, CreateSpecialistModel,String>()
{

    override suspend fun execute(parameters: CreateSpecialistModel,token: String): CreateSubSpecialistResponse {
        return webClient.createSpecialist(token, parameters)
    }

}