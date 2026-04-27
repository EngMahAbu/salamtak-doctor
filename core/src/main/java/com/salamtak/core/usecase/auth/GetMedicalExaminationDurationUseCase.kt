package com.salamtak.core.usecase.auth

import com.salamtak.core.usecase.BaseUseCase
import com.salamtak.domain.entities.response.MedicalExaminationDurationResponse

class GetMedicalExaminationDurationUseCase: BaseUseCase<MedicalExaminationDurationResponse, String, String>() {
    override suspend fun execute(parameters: String, token: String): MedicalExaminationDurationResponse {
        return webClient.getClinicExaminationDuration(token,parameters)
    }
}