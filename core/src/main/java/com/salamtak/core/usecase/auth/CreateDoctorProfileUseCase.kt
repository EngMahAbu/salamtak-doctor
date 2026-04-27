package com.salamtak.core.usecase.auth

import com.salamtak.core.usecase.BaseUseCase
import com.salamtak.domain.entities.response.CreateDoctorProfileResponse
import com.salamtak.domain.entities.resquest.auth.CreateDoctorProfileModel

class CreateDoctorProfileUseCase: BaseUseCase<CreateDoctorProfileResponse,CreateDoctorProfileModel,String>()
{

    override suspend fun execute(parameters: CreateDoctorProfileModel,token: String): CreateDoctorProfileResponse {

        return webClient.createDoctorProfile(token,parameters.token,parameters.doctorProfileModel)
    }

}