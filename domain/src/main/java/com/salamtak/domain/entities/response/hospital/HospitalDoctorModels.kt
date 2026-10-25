package com.salamtak.domain.entities.response.hospital

import com.google.gson.JsonElement

/** Hospital/GetDoctorSchedule?healthEntityId=<clinic id>: slots of every doctor in that clinic. */
data class HospitalScheduleResponse(
    val Data: List<HospitalScheduleSlot>?,
    val Message: String?,
    val MessageCode: Int?,
    val Success: Boolean?
)

data class HospitalScheduleSlot(
    val WorkingDayTimeId: Int,
    val WorkingDayId: Int?,
    val HealthEntityDoctorId: Int?,
    val DayId: Int?,
    val DayName: String?,
    /** 24h "HH:mm" */
    val TimeFrom: String?,
    val TimeTo: String?,
    val Fees: Double?,
    val FollowUpFees: Double?,
    val MaxNoOfPatients: Int?,
    val MedicalExaminationTypeId: Int?
)

/** Body of Hospital/AddDoctorSchedule?doctorId=. */
data class HospitalScheduleRequest(
    val DayId: Int,
    val TimeFrom: String,
    val TimeTo: String,
    val Fees: Double,
    val FollowUp_Fees: Double,
    val DurationMedicalExaminationId: Int,
    val MedicalExaminationTypeId: Int,
    val ClinicId: Int,
    val Inactive: Boolean = false,
    val OverlapApproved: Boolean = false
)

/** Body of Hospital/CreateHospitalClinic (creates the clinic and links it to the doctor). */
data class HospitalCreateClinicRequest(
    val HospitalId: Int,
    val DoctorId: Int,
    val Name: String,
    val NameAr: String,
    val Email: String?,
    val FixedFee: Double?,
    val Phones: List<String>,
    val InfoEn: String?,
    val InfoAr: String?,
    val FeatureIds: List<Int>,
    val ServiceIds: List<Int>
)

/** Body of Hospital/UpdateHospitalClinic; fields not on the Edit Clinic screen are sent back unchanged. */
data class HospitalUpdateClinicRequest(
    val ClinicId: Int,
    val Name: String,
    val NameAr: String,
    val Email: String?,
    val FixedFee: Double?,
    val FollowUp_Period: Int?,
    val Phones: List<String>,
    val InfoEn: String?,
    val InfoAr: String?,
    val FeatureIds: List<Int>,
    val ServiceIds: List<Int>
)

/** Save/create responses whose Data shape is undocumented; read with [JsonElement]. */
data class HospitalDataResponse(
    val Data: JsonElement?,
    val Message: String?,
    val MessageCode: Int?,
    val Success: Boolean?
)

/** PatientHospital/GetDoctorProfile?doctorId=. FirstName/FirstNameAr hold the full name. */
data class HospitalDoctorProfileResponse(
    val Data: HospitalDoctorProfile?,
    val Message: String?,
    val MessageCode: Int?,
    val Success: Boolean?
)

data class HospitalDoctorProfile(
    val Id: Int,
    val UserId: Int?,
    val Image: String?,
    val StatusId: Int?,
    val FirstName: String?,
    val FirstNameAr: String?,
    val MiddelName: String?,
    val MiddelNameAr: String?,
    val LastName: String?,
    val LastNameAr: String?,
    val NickName: String?,
    val NickNameAr: String?,
    val Phone: String?,
    val Email: String?,
    val GenderId: Int?,
    val SpecialistId: Int?,
    val SeniorityLevelId: Int?,
    /** "0001-01-01T00:00:00" when not set */
    val Birthday: String?,
    val NationalityId: Int?,
    val NationalId: String?,
    val SyndicateId: String?,
    val LicenseNumber: String?,
    val DoctorInfo: String?,
    val DoctorInfoAr: String?,
    val DoctorSubSpecialist: List<Int>?
)
