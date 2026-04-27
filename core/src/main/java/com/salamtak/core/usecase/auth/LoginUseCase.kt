package com.salamtak.core.usecase.auth

import com.salamtak.domain.entities.response.UserLoginResponse
import com.salamtak.domain.entities.resquest.auth.LoginModel
import com.salamtak.core.usecase.BaseUseCase

class LoginUseCase : BaseUseCase<UserLoginResponse,LoginModel, String>()
{

    override suspend fun execute(parameters: LoginModel,token: String): UserLoginResponse{

        return webClient.login(token,parameters)

    }

}