package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.hospital

import com.salamtak.core.localDataBase.Localization
import com.salamtak.core.newNetwork.RetrofitBuilder

class HospitalRepository {

    private val client = RetrofitBuilder().instant

    suspend fun getHospitalDashboard(
        hospitalInfoId: Int,
        dateFrom: String? = null,
        dateTo: String? = null,
        clinicId: Int? = null,
        doctorId: Int? = null
    ) = client.getHospitalDashboard(
        Localization.getLanguageName(), hospitalInfoId, dateFrom, dateTo, clinicId, doctorId
    )

    suspend fun getHospitalInfoById(infoId: Int) =
        client.getHospitalInfoById(Localization.getLanguageName(), infoId)

    suspend fun getHospitalClinicList(hospitalId: Int) =
        client.getHospitalClinicList(Localization.getLanguageName(), hospitalId)

    suspend fun getHospitalDoctorList(hospitalId: Int) =
        client.getHospitalDoctorList(Localization.getLanguageName(), hospitalId)

    suspend fun getClinicDoctors(clinicId: Int) =
        client.getHospitalClinicDoctors(Localization.getLanguageName(), clinicId)
}
