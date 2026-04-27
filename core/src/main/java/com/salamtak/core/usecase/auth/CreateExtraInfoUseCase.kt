package com.salamtak.core.usecase.auth

import com.salamtak.core.usecase.BaseUseCase
import com.salamtak.domain.entities.response.CreateDoctorProfileResponse
import com.salamtak.domain.entities.response.CreateExtraInfoResponse
import com.salamtak.domain.entities.resquest.auth.CreateDoctorProfileModel
import com.salamtak.domain.entities.resquest.auth.CreateExtraInfoModel

class CreateExtraInfoUseCase: BaseUseCase<CreateExtraInfoResponse,CreateExtraInfoModel,String>()
{

    override suspend fun execute(parameters: CreateExtraInfoModel, lang : String): CreateExtraInfoResponse {

        return webClient.createExtraInfo(lang, parameters.token, parameters)
    }

}