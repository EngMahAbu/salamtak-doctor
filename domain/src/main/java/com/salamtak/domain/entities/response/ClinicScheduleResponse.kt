package com.salamtak.domain.entities.response

data class ClinicScheduleResponse(
    val Data: List<ClinicScheduleData>,
    val Message: String,
    val MessageCode: Int,
    val Success: Boolean
)

data class ClinicScheduleData(
    val Active: Boolean,
    val DayId: Int,
    val DayName: String,
    val DayPeriodCount: Int,
    val FixedFee: Int,
    var periodSwitch: Boolean = false,// By default Switch is ON but value false
    var overlapping: Boolean = false

)