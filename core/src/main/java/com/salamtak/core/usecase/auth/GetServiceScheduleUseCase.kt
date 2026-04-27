package com.salamtak.core.usecase.auth

import com.salamtak.core.usecase.BaseUseCase
import com.salamtak.domain.entities.response.ClinicScheduleResponse
import com.salamtak.domain.entities.response.ServiceScheduleResponse
import com.salamtak.domain.entities.resquest.auth.MultiQueryModel
import com.salamtak.domain.entities.resquest.auth.QueryModel

class GetServiceScheduleUseCase: BaseUseCase<ServiceScheduleResponse, MultiQueryModel, String>() {
    override suspend fun execute(parameters: MultiQueryModel, token: String): ServiceScheduleResponse {
        return webClient.getServiceSchedule(token,parameters.token,parameters.QueryId,parameters.QueryId2)
    }
}