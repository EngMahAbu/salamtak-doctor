package com.salamtak.domain.entities.response

data class AppointmentResponse(
    val Data: NewsData,
    val Message: String,
    val MessageCode: Int,
    val Success: Boolean
)

data class NewsData(
    val Items: List<Item>,
    val TotalCount: Int
)

data class Item(
    val AppointmentDate: String,
    val AppointmentId: Long,
    val CancelReason: Any,
    val ClinicName: String,
    val ClinicAddress: String,
    val ClinicPhone: List<String>,
    val HaveEMR: Boolean,
    var IsCancel: Boolean,
    var IsCompleted: Boolean,
    var IsNoShow: Boolean,
    val MedicalExaminationTypeId: Int,
    val MedicalExaminationTypeName: String,
    val MedicalExaminationTypeImage: String,
    val PatientId: Long,
    val PatientImage: String,
    val PatientName: String,
    val AnotherPatientFullName: String,
    val RelationshipTypeName: String,
    val RelationshipTypeId: String,
    val PatientPhone: String,
    val CanCancel: Boolean,
    val AppointmentTime: String,
    val DoctorName: String,
    val SpecName: String,
    val DoctorImage: String,

)