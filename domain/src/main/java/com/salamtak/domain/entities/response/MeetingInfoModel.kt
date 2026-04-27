package com.salamtak.domain.entities.response

data class MeetingInfoModel(
    val Data: StartMeeting,
    val Message: String,
    val MessageCode: Int,
    val Success: Boolean
)

data class StartMeeting(
    val AppointmentID: Int,
    val EndDate: String,
    val ID: Int,
    val StartDate: String,
    val ZoomIsUsed: Boolean,
    val ZoomJoinURL: String,
    val ZoomStartURL: String,
    val host_email: String,
    val host_id: String,
    val topic: String,
    val zoomMeetID: String,
    val zoomMeetPassword: String
)