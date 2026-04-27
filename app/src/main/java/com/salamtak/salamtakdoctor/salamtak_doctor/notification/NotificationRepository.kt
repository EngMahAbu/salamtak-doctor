package com.salamtak.salamtakdoctor.salamtak_doctor.notification

import com.salamtak.core.localDataBase.Localization
import com.salamtak.core.newNetwork.RetrofitBuilder

class NotificationRepository {

    private val client = RetrofitBuilder().instant

    suspend fun getNotification() = client.getNotification(Localization.getLanguageName())

}