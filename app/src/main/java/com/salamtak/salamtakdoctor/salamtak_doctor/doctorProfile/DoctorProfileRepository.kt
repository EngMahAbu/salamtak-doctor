package com.salamtak.salamtakdoctor.salamtak_doctor.doctorProfile

import com.salamtak.core.localDataBase.Localization
import com.salamtak.core.newNetwork.RetrofitBuilder
import com.salamtak.domain.entities.resquest.auth.UpdateDoctorProfileModel

class DoctorProfileRepository {
    private val client = RetrofitBuilder().instant

    suspend fun getDoctorProfile() = client.getDoctorProfile(Localization.getLanguageName())
    suspend fun getDoctorProfileByID(doctorId : Int) = client.getDoctorProfileByID(Localization.getLanguageName(), doctorId)

    suspend fun deleteUser() = client.DeleteProfile(Localization.getLanguageName())

    //suspend fun editDoctorProfile(updateDoctorProfileModel: UpdateDoctorProfileModel) = client.up(updateDoctorProfileModel)

}