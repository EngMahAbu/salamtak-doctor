package com.salamtak.core.usecase.auth

import com.salamtak.core.usecase.BaseUseCase
import com.salamtak.domain.entities.response.CitiesResponse
import com.salamtak.domain.entities.response.LocationDataModel
import com.salamtak.domain.entities.resquest.auth.LocationModel

class LocationUseCase: BaseUseCase<LocationDataModel, LocationModel, String>(){
    override suspend fun execute(parameters: LocationModel, token: String): LocationDataModel {
        return webClient.setLocation(token,parameters.token,parameters)
    }
}