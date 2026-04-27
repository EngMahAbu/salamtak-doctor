package com.salamtak.domain.entities.response

data class MedicalExaminationDurationResponse(
    val Data: List<MedicalExaminationDurationData>,
    val Message: String,
    val MessageCode: Int,
    val Success: Boolean
)

data class MedicalExaminationDurationData(
    val Duration: Int,
    val Id: Int
)