package com.salamtak.domain.entities.response

data class ServiceDayScheduleResponse(
    val Data: List<ServiceDayScheduleData>,
    val Message: String,
    val MessageCode: Int,
    val Success: Boolean
)

data class ServiceDayScheduleData(
    val DayId: Int,
    val DayName: String,
    val DurationMedicalExaminationId: Int,
    val Fees: Int,
    val SchedualId: Int,
    val TimeFrom: String,
    val MedicalExaminationTypeName: String,
    val MedicalExaminationTypeImage: String,
    val TimeTo: String,
    val MaxNoOfPatients: Int,
    val TimeInterval: Int,
    val FollowUp_Fees: Int,
    var Inactive: Boolean,
    var OverlapApproved : Boolean,
    val WorkingDate:String
)