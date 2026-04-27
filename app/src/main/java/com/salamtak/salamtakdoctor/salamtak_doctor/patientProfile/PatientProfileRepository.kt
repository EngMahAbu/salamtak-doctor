package com.salamtak.salamtakdoctor.salamtak_doctor.patientProfile

import com.salamtak.core.localDataBase.Localization
import com.salamtak.core.newNetwork.RetrofitBuilder
import com.salamtak.domain.entities.resquest.auth.EmrDescriptionModel
import com.salamtak.domain.entities.resquest.auth.EmrInstructionModel
import okhttp3.MultipartBody
import okhttp3.RequestBody

class PatientProfileRepository {


    private val client = RetrofitBuilder().instant

    suspend fun getPatientProfile(appointmentId: Int) = client.getPatientProfile(Localization.getLanguageName(),appointmentId)

    suspend fun getPatientEmr(patientId: Int) = client.getPatientEmr(Localization.getLanguageName(),patientId)

    suspend fun getEmrDetails(appointmentId: Int) = client.getEmrDetails(Localization.getLanguageName(),appointmentId)

    suspend fun setEmrInstruction(emrInstructionModel: EmrInstructionModel) =
        client.setEmrInstruction(Localization.getLanguageName(),emrInstructionModel)

    suspend fun setEmrDescription(emrDescriptionModel: EmrDescriptionModel) =
        client.setEmrDescription(Localization.getLanguageName(),emrDescriptionModel)

//    suspend fun sendEmrAttachment(image: MultipartBody.Part?, appointmentId: RequestBody) =
//        client.sendAttachmentEmr(image, appointmentId)

    suspend fun getPatientMedical(patientId:Int) = client.getPatientMedicalData(Localization.getLanguageName() , patientId)

}