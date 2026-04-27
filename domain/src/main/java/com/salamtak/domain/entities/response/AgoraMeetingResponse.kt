package com.salamtak.domain.entities.response

data class AgoraMeetingResponse (
    val Data: AgoraMeetingData,
    val Message: String,
    val MessageCode: Int,
    val Success: Boolean
        )

data class AgoraMeetingData(
    val AppointmentId : Int,
    val channelName : String,
    val appCertificate : String ?= null,
    val appId : String ?= null,
    val Token : String,
    val Token2 : String ?= null,
    val PatientName : String,
    val PatientImage : String,
    val ThreadID : String

)