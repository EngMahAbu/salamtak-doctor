package com.salamtak.core.usecase.auth

import com.salamtak.core.usecase.BaseUseCase
import com.salamtak.domain.entities.response.CountriesResponse
import com.salamtak.domain.entities.response.SubSpecialitiesResponse
import com.salamtak.domain.entities.response.UserLoginResponse
import com.salamtak.domain.entities.resquest.auth.LoginModel

class GetNationalitiesUseCase: BaseUseCase<CountriesResponse,String,String>()
{

    override suspend fun execute(parameters: String,token: String): CountriesResponse {
        return webClient.getCountries(token,parameters)
    }

}