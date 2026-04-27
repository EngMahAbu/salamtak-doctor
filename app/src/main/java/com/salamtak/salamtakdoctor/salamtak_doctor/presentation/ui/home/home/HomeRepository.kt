package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.home

import com.salamtak.core.localDataBase.Localization
import com.salamtak.core.newNetwork.RetrofitBuilder
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.DateUtils

class HomeRepository {
    private val client = RetrofitBuilder().instant

    suspend fun getHome(fromDate: String? = null, toDate: String? = null, clinicID: Int? = null) =
        client.getHome(DateUtils.getLang()!!,fromDate, toDate, clinicID)
    suspend fun getPolyClinicDashboard(fromDate: String? = null, toDate: String? = null, clinicID: Int? = null,DoctorID: Int? = null,PolyInfoID: Int) =
        client.getPolyClinicDashboard(DateUtils.getLang()!!,fromDate, toDate, clinicID,DoctorID,PolyInfoID)

    suspend fun getHomeClinic() = client.getHomeClinics(DateUtils.getLang()!!)

    suspend fun getWhatsUpAds() = client.getWhatsUpAds(Localization.getLanguageName(),true)

    suspend fun getHomeAds() = client.getHomeAds(Localization.getLanguageName(),1,true)
    suspend fun getDoctorProfile() = client.getDoctorProfile(DateUtils.getLang()!!)

}