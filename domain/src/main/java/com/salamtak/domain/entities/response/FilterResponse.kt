package com.salamtak.domain.entities.response

data class FilterResponse(
    val Data: FilterData,
    val Message: String,
    val MessageCode: Int,
    val Success: Boolean
)

data class FilterData(
    val Items: List<FilterItem>,
    val TotalCount: Int
)


data class FilterItem(
    val AppointmentDate: String,
    val AppointmentId: Long,
    val CancelReason: Any,
    val ClinicName: String,
    val HaveEMR: Boolean,
    val IsCancel: Boolean,
    val MedicalExaminationTypeId: Int,
    val MedicalExaminationTypeName: String,
    val PatientId: Long,
    val PatientImage: String,
    val PatientName: String
)