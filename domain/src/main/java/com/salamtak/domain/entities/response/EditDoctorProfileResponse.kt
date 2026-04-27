package com.salamtak.domain.entities.response

data class EditDoctorProfileResponse(
    val Data: NewData,
    val Message: String,
    val MessageCode: Int,
    val Success: Boolean
)

data class NewData(
//    val Birthday: String,
    val Birthday: String,
    val Code: String,
    val DoctorInfo: String,
    val DoctorInfoAr: String,
    val DoctorSubSpecialist: List<Int>,
    val FirstName: String,
    val FirstNameAr: String,
    val GenderId: Int,
    val Id: Int,
    val Image: String,
    val LastName: String,
    val LastNameAr: String,
    val MiddelName: String,
    val MiddelNameAr: String,
    val NationalityId: Int,
    val ProfileStatus: Int,
    val SeniorityLevelId: Int,
    val SpecialistId: Int,
    val StatusId: Int,
    val Website: String,
    val NationalityName: String,
    val SpecialistName: String,
    val GenderName: String,
    val SeniorityLevelName: String,
    val SubSpecialistName: List<String>,
    val Email: String,
    val NationalId: String,
    val SyndicateId: String,
    val FacebookAccount: String,
    val LicenseNumber: String,
    val Phone: String,
    val NickName:String?,
    val NickNameAr:String?,
    val StatusName:String?
)