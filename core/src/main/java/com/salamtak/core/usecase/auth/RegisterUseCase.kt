package com.salamtak.core.usecase.auth

import com.salamtak.domain.entities.resquest.auth.RegisterModel
import com.salamtak.core.usecase.BaseUseCase
import com.salamtak.domain.entities.response.RegisterResponse

class RegisterUseCase: BaseUseCase<RegisterResponse , RegisterModel,String>() {

    override suspend fun execute(parameters: RegisterModel,token: String): RegisterResponse {

        return webClient.register(token,parameters)

    }

}