package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.appointments.filter

import com.salamtak.core.localDataBase.Localization
import com.salamtak.core.newNetwork.RetrofitBuilder
import com.salamtak.domain.entities.resquest.auth.FilterModel

class FilterRepository {

    private val client = RetrofitBuilder().instant

    suspend fun filter(filterModel: FilterModel) = client.SendFilterData(Localization.getLanguageName(),filterModel)
    suspend fun startMeeting(token:String,appointmentId:String) = client.startMeeting(Localization.getLanguageName(),token, appointmentId)
    suspend fun updateMeeting(appointmentId:String) = client.updateMeeting(Localization.getLanguageName(), appointmentId)
}