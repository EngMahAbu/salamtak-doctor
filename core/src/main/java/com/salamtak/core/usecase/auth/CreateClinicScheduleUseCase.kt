package com.salamtak.core.usecase.auth

import com.salamtak.core.usecase.BaseUseCase
import com.salamtak.domain.entities.response.DefaultResponse
import com.salamtak.domain.entities.resquest.auth.CreateClinicSchedule

class CreateClinicScheduleUseCase: BaseUseCase<DefaultResponse,CreateClinicSchedule,String>() {
    override suspend fun execute(parameters: CreateClinicSchedule, token: String): DefaultResponse {
        return webClient.createClinicSchedule(token,parameters.token,parameters.clinicScheduleModel)
    }
}