package com.salamtak.domain.entities.response

data class HomeResponse(
    val Data: HomeData,
    val Message: String,
    val MessageCode: Int,
    val Success: Boolean
)

data class HomeData(
    val ClinicId: Int,
    val TodayAppointmentCount: Int,
    val TotalAppointmentCount: Int,
    val UpComingAppointmentCount: Int,
    val getMedicalEximationListDtos: List<GetMedicalEximationDtos>
)

data class GetMedicalEximationDtos(
    val Image: String,
    val MedicalTypeCount: Int,
    val MedicalTypeId: Int,
    val MedicalTypeName: String
)