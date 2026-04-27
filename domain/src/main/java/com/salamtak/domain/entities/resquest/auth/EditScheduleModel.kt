package com.salamtak.domain.entities.resquest.auth

data class EditScheduleModel(
    val token: String,
    val scheduleModel: ScheduleModel
)

data class ScheduleModel(
    val DayId: Int,
    val TimeFrom: String,
    val TimeTo: String,
    val Fees: Double,
    val FollowUp_Fees: Double,
    val SchedualId: Int,
    val MaxNoOfPatients:Int,
    val MedicalExaminationTypeId:Int,
//    val DurationMedicalExaminationId: Int,
    val ClinicId: Int,
    var Inactive: Boolean,
    var OverlapApproved : Boolean,
    val workDate:String?,
)
