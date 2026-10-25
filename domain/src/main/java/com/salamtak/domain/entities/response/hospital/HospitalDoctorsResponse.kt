package com.salamtak.domain.entities.response.hospital

/** Shape shared by Hospital/GetHospitalDoctorList and Hospital/GetClinicDoctors. */
data class HospitalDoctorsResponse(
    val Data: List<HospitalDoctor>?,
    val Message: String?,
    val MessageCode: Int?,
    val Success: Boolean?
)

data class HospitalDoctor(
    val HealthEntityDoctorId: Int?,
    val DoctorId: Int,
    val DoctorName: String?,
    val DoctorNameAr: String?,
    val DoctorImage: String?,
    val SpecialistId: Int?,
    val SpecialistName: String?,
    val SubSpecialistName: List<String>?,
    val SeniorityLevelId: Int?,
    val SeniorityLevelName: String?,
    val StatusId: Int?,
    val AvgRate: Double?,
    val RateCount: Int?
)
