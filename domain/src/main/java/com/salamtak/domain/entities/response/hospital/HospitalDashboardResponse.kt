package com.salamtak.domain.entities.response.hospital

import com.salamtak.domain.entities.response.GetMedicalEximationDtos

/** Hospital/GetHospitalDashboard: same shape as HomeResponse plus hospital extras. */
data class HospitalDashboardResponse(
    val Data: HospitalDashboardData?,
    val Message: String?,
    val MessageCode: Int?,
    val Success: Boolean?
)

data class HospitalDashboardData(
    val TotalAppointmentCount: Int?,
    val UpComingAppointmentCount: Int?,
    val TodayAppointmentCount: Int?,
    val ClinicId: Int?,
    val ClinicsCount: Int?,
    val StatusId: Int?,
    val Notification_Count: Int?,
    val getMedicalEximationListDtos: List<GetMedicalEximationDtos>?
)
