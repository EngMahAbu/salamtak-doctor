package com.salamtak.domain.entities.response
import java.util.ArrayList
data class EmrTitleResponse(
    val Data: List<EmrTitleData>,
    val Message: String,
    val MessageCode: Int,
    val Success: Boolean
)

data class EmrTitleData(
    val AppointmentId: Int,
    val AppountmentDate: String,
    val DoctorName: String,
    val DoctorSpeciality: String,
    val PatientName: String,
    val PatientPhone: String,
    val ClinicAddress: String,
    val ClinicPhone: List<String>,
    val EmrId: Int,
    val Instructions: String,
    val MedicalExaminationTypeId: Int,
    val MedicalExaminationTypeName: String,
    val PatientEmrdetails: List<NewPatientEmrdetail>,
    val PatientEmrdocuments: ArrayList<NewPatientEmrdocument>,
    val ClinicName: String
)

data class NewPatientEmrdetail(
    val Description: String,
    val Id: Int,
    val Title: String
)

data class NewPatientEmrdocument(
    val DocumentUrl: String,
    val Id: Int
)

//    (
//    val Data: List<EmrTitleData>,
//    val Message: String,
//    val MessageCode: Int,
//    val Success: Boolean
//)
//
//data class EmrTitleData(
//    val AppointmentId: Int,
//    val AppountmentDate: String,
//    val EmrId: Int,
//    val MedicalExaminationTypeId: Int,
//    val MedicalExaminationTypeName: String
//)