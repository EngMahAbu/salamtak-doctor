package com.salamtak.domain.entities.response

data class ClinicDayScheduleResponse(
    val Data: List<ClinicDayScheduleData>,
    val Message: String,
    val MessageCode: Int,
    val Success: Boolean
)

data class ClinicDayScheduleData(
    val DayId: Int,
    val DayName: String,
    val DurationMedicalExaminationId: Int,
    val Fees: Int,
    val SchedualId: Int,
    val TimeFrom: String,
    val TimeTo: String,
    val Inactive: Boolean,
    var OverlapApproved : Boolean
)