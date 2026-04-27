package com.salamtak.domain.entities.resquest.auth

data class CreateDoctorServiceSchedule(
    val token: String,
    val doctorServiceSchedule: DoctorServiceSchedule
)

data class DoctorServiceSchedule(
    val DayId: Int,
    val TimeFrom: String,
    val TimeTo: String,
    val Fees: Double,
    var MedicalExaminationTypeId: Int,
    val ClinicId: Int,
    val DoctorId: Int?=null,
    val DurationMedicalExaminationId: Int,
    var FollowUp_Fees: Double?=null,
    val Inactive: Boolean = false,
    val OverlapApproved : Boolean?=false,
    val WorkingDate:String?,

)