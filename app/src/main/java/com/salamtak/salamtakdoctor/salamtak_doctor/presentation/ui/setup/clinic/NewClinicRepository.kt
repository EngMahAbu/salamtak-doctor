package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.clinic

import com.salamtak.core.localDataBase.Localization
import com.salamtak.core.newNetwork.RetrofitBuilder

class NewClinicRepository {
    private val client = RetrofitBuilder().instant

    suspend fun getClinicInfo(clinicId: Int) = client.getClinicInfo(Localization.getLanguageName(),clinicId)
    suspend fun getInfo(clinicId: Int) = client.getInfo(Localization.getLanguageName(),clinicId)
}