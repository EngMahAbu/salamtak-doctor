package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.clinic.vacation

import com.salamtak.core.localDataBase.Localization
import com.salamtak.core.newNetwork.RetrofitBuilder
import com.salamtak.domain.entities.resquest.auth.PatientAccountModel
import com.salamtak.domain.entities.resquest.auth.VactionModel

class VacationRepository {

    private val client = RetrofitBuilder().instant

    suspend fun vaction(clinicID: Int) = client.getVaction(Localization.getLanguageName(), clinicID)

    suspend fun addVaction(vactionModel: VactionModel) =
        client.addVaction(Localization.getLanguageName(), vactionModel)

    suspend fun editVaction(vactionModel: VactionModel) =
        client.editVaction(Localization.getLanguageName(), vactionModel)

    suspend fun deleteVaction(doctorVacationId: Int) =
        client.deleteVaction(Localization.getLanguageName(), doctorVacationId)
}