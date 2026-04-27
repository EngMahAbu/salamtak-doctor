package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.appointments.patientAccount

import com.salamtak.core.localDataBase.Localization
import com.salamtak.core.newNetwork.RetrofitBuilder
import com.salamtak.domain.entities.resquest.auth.PatientAccountModel

class PatientAccountRepository {

    private val client = RetrofitBuilder().instant

    suspend fun patientAccount(patientId: Int) =
        client.getPatientAccount(Localization.getLanguageName(),patientId)

    suspend fun addPatientAccount(patientAccountModel: PatientAccountModel) =
        client.addPatientAccount(Localization.getLanguageName(),patientAccountModel)

    suspend fun editPatientAccount(patientAccountModel: PatientAccountModel) =
        client.editPatientAccount(Localization.getLanguageName(),patientAccountModel)

    suspend fun deletePatientAccount(dataId : Int) =
        client.deletePatientAccount(Localization.getLanguageName(),dataId)

}