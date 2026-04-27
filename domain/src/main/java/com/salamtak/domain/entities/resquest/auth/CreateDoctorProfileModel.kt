package com.salamtak.domain.entities.resquest.auth

import java.io.File

data class CreateDoctorProfileModel(
    val token: String,
    val doctorProfileModel: DoctorProfileModel
)

data class DoctorProfileModel(
    val UserId: String,
    val FirstName: String,
    val FirstNameAr: String,
    val MiddelName: String,
    val MiddelNameAr: String,
    val LastName: String,
    val LastNameAr: String,
    val GenderId: String,
    val Birthday: String,
    val Image: String,
//    val Website: String,
    val DoctorInfo: String,
    val DoctorInfoAr: String,
    val NationalityId: String,
    val DoctorSubSpecialist: List<Int>,
    val profileImage: File? = null,
    val NationalId: String,
    val SyndicateId:String,
    val LicenseNumber:String,
    val NickName:String,
    val NickNameAr:String
    // val Birthday: String
)
