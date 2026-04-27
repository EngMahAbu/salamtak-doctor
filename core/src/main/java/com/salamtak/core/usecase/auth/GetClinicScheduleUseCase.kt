package com.salamtak.core.usecase.auth

import com.salamtak.core.usecase.BaseUseCase
import com.salamtak.domain.entities.response.ClinicScheduleResponse
import com.salamtak.domain.entities.resquest.auth.QueryModel

class GetClinicScheduleUseCase: BaseUseCase<ClinicScheduleResponse, QueryModel, String>() {
    override suspend fun execute(parameters: QueryModel, token: String): ClinicScheduleResponse {
        return webClient.getClinicSchedule(token,parameters.token,parameters.QueryId)
    }
}