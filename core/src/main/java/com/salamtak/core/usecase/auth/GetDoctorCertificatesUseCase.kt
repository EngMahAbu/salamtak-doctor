package com.salamtak.core.usecase.auth

import com.salamtak.core.usecase.BaseUseCase
import com.salamtak.domain.entities.response.CertificatesResponse

class GetDoctorCertificatesUseCase: BaseUseCase<CertificatesResponse,String,String>() {
    override suspend fun execute(parameters: String, token: String): CertificatesResponse {
        return webClient.getCertificates(token,parameters)
    }


}