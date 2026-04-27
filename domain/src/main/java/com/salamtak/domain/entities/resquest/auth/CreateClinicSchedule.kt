package com.salamtak.domain.entities.resquest.auth

data class CreateClinicSchedule(
    val token: String,
    val clinicScheduleModel: ClinicScheduleModel
)

data class ClinicScheduleModel(
    val DayId: Int,
    val TimeFrom: String,
    val TimeTo: String,
    val Fees: Double,
    val DurationMedicalExaminationId: Int,
    val ClinicId: Int,
    val MedicalExaminationTypeId: Int,
    var FollowUp_Fees: Double? = null,
    var Inactive: Boolean = true,
    var OverlapApproved: Boolean = false
)
