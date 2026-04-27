package com.salamtak.core.newNetwork

import com.salamtak.core.usecase.UseCaseModule
import com.salamtak.domain.entities.response.EditDoctorProfileResponse
import retrofit2.http.GET

interface Apis {

    //new Artuchture
    @GET(UseCaseModule.GET_DOCTOR_PROFILE)
    suspend fun getPatientProfile(): NetworkResponse<EditDoctorProfileResponse, ErrorModel>

}