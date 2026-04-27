package com.salamtak.domain.entities.resquest.auth

import java.io.File

data class UpdateDoctorProfileModel(
//    var DoctorInfo: String? = null,
//    var DoctorInfoAr: String? = null,
//    var Email: String? = null,
//    var Birthday: String? = null
    val FirstName: String,
    val FirstNameAr: String,
    val MiddelName: String,
    val MiddelNameAr: String,
    val LastName: String,
    val LastNameAr: String,
    val GenderId: String,
    val SpecialistId: Int,
    val Birthday: String,
    val SeniorityLevelId: Int,
//    val Website: String,
    val DoctorInfo: String,
    val DoctorInfoAr: String,
    val NationalityId: String,
    val DoctorSubSpecialist: List<Int>,
    val profileImage: File? = null,
    val Email: String,
    val NationalId: String,
    val SyndicateId: String,
    val LicenseNumber: String,
    val NickName:String,
    val NickNameAr:String
//    val FacebookAccount: String
//    val Birthday: String
    )