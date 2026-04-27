package com.salamtak.core.usecase.auth

import com.salamtak.core.usecase.BaseUseCase
import com.salamtak.domain.entities.response.ClinicDayScheduleResponse
import com.salamtak.domain.entities.resquest.auth.MultiQueryModel

class GetClinicDayScheduleUseCase: BaseUseCase<ClinicDayScheduleResponse, MultiQueryModel, String>() {
    override suspend fun execute(parameters: MultiQueryModel, token: String): ClinicDayScheduleResponse {
        return webClient.getClinicDaySchedule(token,parameters.token,parameters.QueryId,parameters.QueryId2)
    }
}