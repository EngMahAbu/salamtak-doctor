package com.salamtak.core.usecase.auth

import com.salamtak.core.usecase.BaseUseCase
import com.salamtak.domain.entities.response.DefaultResponse
import com.salamtak.domain.entities.resquest.auth.CreateClinicSchedule
import com.salamtak.domain.entities.resquest.auth.CreateDoctorServiceSchedule

class CreateDoctorServiceScheduleUseCase: BaseUseCase<DefaultResponse,CreateDoctorServiceSchedule,String>() {
    override suspend fun execute(parameters: CreateDoctorServiceSchedule, token: String): DefaultResponse {
        return webClient.createServiceSchedule(token,parameters.token,parameters.doctorServiceSchedule)
    }
}