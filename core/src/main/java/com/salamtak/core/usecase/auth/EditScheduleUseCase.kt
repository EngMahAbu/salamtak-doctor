package com.salamtak.core.usecase.auth

import com.salamtak.core.usecase.BaseUseCase
import com.salamtak.domain.entities.response.DefaultResponse
import com.salamtak.domain.entities.resquest.auth.EditScheduleModel

class EditScheduleUseCase: BaseUseCase<DefaultResponse, EditScheduleModel,String>() {
    override suspend fun execute(parameters: EditScheduleModel, token: String): DefaultResponse {
        return webClient.editClinicSchedule(token,parameters.token,parameters.scheduleModel)
    }
}