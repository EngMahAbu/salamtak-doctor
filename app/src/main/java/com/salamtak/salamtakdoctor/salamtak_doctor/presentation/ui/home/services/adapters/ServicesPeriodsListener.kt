package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.services.adapters

import com.salamtak.domain.entities.response.ServiceDayScheduleResponse


interface ServicesPeriodsListener {

    fun result(response: ServiceDayScheduleResponse)

}