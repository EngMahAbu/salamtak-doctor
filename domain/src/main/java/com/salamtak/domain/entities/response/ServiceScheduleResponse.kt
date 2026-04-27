package com.salamtak.domain.entities.response

data class ServiceScheduleResponse(
    val Data: List<ServiceScheduleData>,
    val Message: String,
    val MessageCode: Int,
    val Success: Boolean
)

data class ServiceScheduleData(
    val Active: Boolean,
    val DayId: Int,
    val DayName: String,
    val DayPeriodCount: Int,
    val MaxNoOfPatients: Int,
    val FixedFee: Int,
    var Inactive : Boolean,
    var OverlapApproved :Boolean,
    val WorkingDate:String
)