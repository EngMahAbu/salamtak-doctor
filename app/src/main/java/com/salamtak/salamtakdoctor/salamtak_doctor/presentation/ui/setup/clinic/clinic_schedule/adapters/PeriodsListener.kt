package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.clinic.clinic_schedule.adapters

import com.salamtak.domain.entities.response.ClinicDayScheduleResponse


interface PeriodsListener {

    fun result(response: ClinicDayScheduleResponse)

}