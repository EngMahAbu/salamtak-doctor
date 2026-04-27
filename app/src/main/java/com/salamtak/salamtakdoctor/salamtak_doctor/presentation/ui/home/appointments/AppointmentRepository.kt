package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.appointments

import com.salamtak.core.localDataBase.Localization
import com.salamtak.core.newNetwork.RetrofitBuilder

class AppointmentRepository {

    private val client = RetrofitBuilder().instant


    suspend fun getAgoraAppId()= client.getAgoraAppId(Localization.getLanguageName())
    suspend fun getAgoraMeeting(appointmentId : Int)= client.getAgoraMeeting(Localization.getLanguageName(),
    appointmentId)

    suspend fun getCurrentAppointment(
        fromDate: String? = null,
        toDate: String? = null,
        clinicID: Int? = null,
        medicalTypeId: Int? = null,
        skipCount: Int? = null,
        maxResultCount: Int? = null
    ) = client.getCurrentAppointment(Localization.getLanguageName(),
        fromDate,
        toDate,
        clinicID,
        medicalTypeId,
        skipCount,
        maxResultCount
    )

    suspend fun getCurrentDoctorAppointmentPolyclinic(
        fromDate: String? = null,
        toDate: String? = null,
        clinicID: Int? = null,
        medicalTypeId: Int? = null,
        skipCount: Int? = null,
        maxResultCount: Int? = null,
        polyInfoID: Int? = null,
        doctorID: Int? = null,
    ) = client.getCurrentDoctorAppointmentPolyclinic(Localization.getLanguageName(),
        fromDate,
        toDate,
        clinicID,
        medicalTypeId,
        skipCount,
        maxResultCount,polyInfoID,doctorID
    )

    suspend fun getHistoryAppointment() = client.getHistoryAppointment(Localization.getLanguageName())

    suspend fun cancelAppointment(appointmentId: Int, cancelReason: String?=null) = client.cancelAppointment(Localization.getLanguageName(),appointmentId,cancelReason)
    suspend fun changeAppointmentStatus(appointmentId: Int, statusId: Int?=null) = client.changeAppointmentStatus(Localization.getLanguageName(),appointmentId,statusId)

//    suspend fun sendCodeAgain(sendCodeAgainModel: SendCodeAgainModel) = client.sendCodeAgain(sendCodeAgainModel)

}