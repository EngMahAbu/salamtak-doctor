package com.salamtak.core.usecase.auth

import com.salamtak.core.usecase.BaseUseCase
import com.salamtak.domain.entities.response.ClinicScheduleResponse
import com.salamtak.domain.entities.response.ServiceScheduleResponse
import com.salamtak.domain.entities.resquest.auth.MultiQueryModel
import com.salamtak.domain.entities.resquest.auth.QueryModel

class PolyClinicGetServiceScheduleUseCase: BaseUseCase<ServiceScheduleResponse, MultiQueryModel, String>() {
    override suspend fun execute(parameters: MultiQueryModel, lang: String): ServiceScheduleResponse {
        return webClient.getPolyServiceSchedule(lang,parameters.token,parameters.QueryId,parameters.QueryId2)
    }
}