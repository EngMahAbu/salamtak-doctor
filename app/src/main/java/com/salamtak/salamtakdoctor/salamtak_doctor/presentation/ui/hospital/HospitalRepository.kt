package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.hospital

import com.salamtak.core.localDataBase.Localization
import com.salamtak.core.newNetwork.RetrofitBuilder
import com.salamtak.domain.entities.response.hospital.HospitalBranchRequest
import okhttp3.RequestBody

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

    /** [culture] matters: Bio, city/area and service names come back in that language. */
    suspend fun getHospitalInfoById(infoId: Int, culture: String = Localization.getLanguageName()) =
        client.getHospitalInfoById(culture, infoId)

    suspend fun getHospitalClinicList(hospitalId: Int) =
        client.getHospitalClinicList(Localization.getLanguageName(), hospitalId)

    suspend fun getHospitalDoctorList(hospitalId: Int) =
        client.getHospitalDoctorList(Localization.getLanguageName(), hospitalId)

    suspend fun getClinicDoctors(clinicId: Int) =
        client.getHospitalClinicDoctors(Localization.getLanguageName(), clinicId)

    suspend fun getHospitalOwner(hospitalId: Int) =
        client.getHospitalOwner(Localization.getLanguageName(), hospitalId)

    suspend fun updateHospitalOwner(body: RequestBody) =
        client.updateHospitalOwner(Localization.getLanguageName(), body)

    suspend fun updateHospitalInfo(body: RequestBody) =
        client.updateHospitalInfo(Localization.getLanguageName(), body)

    suspend fun getHospitalBranches(hospitalId: Int) =
        client.getHospitalBranches(Localization.getLanguageName(), hospitalId)

    suspend fun createHospitalBranch(request: HospitalBranchRequest) =
        client.createHospitalBranch(Localization.getLanguageName(), request)

    suspend fun updateHospitalBranch(request: HospitalBranchRequest) =
        client.updateHospitalBranch(Localization.getLanguageName(), request)

    suspend fun deleteHospitalBranch(branchId: Int) =
        client.deleteHospitalBranch(Localization.getLanguageName(), branchId)

    suspend fun getAllServices() = client.getAllServices(Localization.getLanguageName())

    suspend fun getAllFeatures() = client.getAllFeatures(Localization.getLanguageName())

    suspend fun getAllCities() = client.getAllCities(Localization.getLanguageName())

    suspend fun getAreasByCityId(cityId: Int) =
        client.getAreasByCityId(Localization.getLanguageName(), cityId)
}
