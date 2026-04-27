package com.salamtak.domain.entities.response

data class EmrInstructionResponse(
    val Data: EmrInstructionData,
    val Message: String,
    val MessageCode: Int,
    val Success: Boolean
)

data class EmrInstructionData(
    val AppointmentId: Int,
    val Id: Int,
    val Instructions: String
)