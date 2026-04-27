package com.salamtak.core.usecase.auth

import com.salamtak.domain.entities.response.UserLoginResponse
import com.salamtak.core.usecase.BaseUseCase
import com.salamtak.domain.entities.resquest.auth.RegisterModel

class CreateUserUseCase : BaseUseCase<UserLoginResponse,RegisterModel,String>()
{
    override suspend fun execute(parameters: RegisterModel, token: String): UserLoginResponse {
        return webClient.createUser(token,parameters)
    }


}