package com.salamtak.domain.entities.response

data class NotificationResponse(
    val Data: List<NotificationData>,
    val Message: String,
    val MessageCode: Int,
    val Success: Boolean
)

data class NotificationData(
    val AppointmentDate: String,
    val AppointmentId: Int,
    val CanRate: Boolean,
    val ClinicLatitude: String,
    val ClinicLongitude: String,
    val ClinicName: String,
    val DoctorId: Int,
    val DoctorImage: String,
    val DoctorName: String,
    val IsCancel: Boolean,
    val IsSeen: Boolean,
    val MedicalExaminationTypeId: Int,
    val MedicalExaminationTypeName: String,
    val SeniorityLevelName: String,
    val SpecialistName: String
)