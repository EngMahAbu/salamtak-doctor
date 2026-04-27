package com.salamtak.core.usecase.auth

import com.salamtak.core.usecase.BaseUseCase
import com.salamtak.domain.entities.response.SubSpecialitiesResponse
import com.salamtak.domain.entities.response.UserLoginResponse
import com.salamtak.domain.entities.resquest.auth.GetSubSpecialitiesModel
import com.salamtak.domain.entities.resquest.auth.LoginModel

class GetSubSpecialitiesUseCase: BaseUseCase<SubSpecialitiesResponse, GetSubSpecialitiesModel,String>()
{

    override suspend fun execute(parameters: GetSubSpecialitiesModel,token: String): SubSpecialitiesResponse {
        return webClient.getSubSpecialities(token,parameters)
    }

}