package com.salamtak.domain.entities.resquest.auth

data class CreateExtraInfoModel(
    val token : String,
    val data: ExtraInfoData
)

data class ExtraInfoData(
    val SeniorityLevelId: Int,
    val SpecialistId: Int,
    val DoctorSubSpecialist: List<Int>,
    val DoctorInfo: String,
    val DoctorInfoAr: String,
    val DoctorID:Int?
)