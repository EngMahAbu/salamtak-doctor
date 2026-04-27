package com.salamtak.domain.entities


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class QuestionsResponse(
    @SerializedName("Data")
    val data: List<Questions?>?,
    @SerializedName("Message")
    val message: String?,
    @SerializedName("MessageCode")
    val messageCode: Int?,
    @SerializedName("Success")
    val success: Boolean?
)
data class Questions(
    @SerializedName("Age")
    val age: Int?,
    @SerializedName("Answer")
    val answer: String?,
    @SerializedName("DoctorID")
    val doctorID: Int?,
    @SerializedName("GenderID")
    val genderID: Int?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("IsForMe")
    val isForMe: Boolean?,
    @SerializedName("PatientID")
    val patientID: Int?,
    @SerializedName("QDetails")
    val qDetails: String?,
    @SerializedName("Question")
    val question: String?,
    @SerializedName("QDate")
    val qDate: String?,
    @SerializedName("SpecialistID")
    val specialistID: Int?,
    @SerializedName("SpecialityName")
    val specialityName: String?,
    @SerializedName("PatientFirstname")
    val patientFirstname: String?,
    @SerializedName("PatientFulltname")
    val patientFulltname: String?,
    @SerializedName("Chronic_disease")
    val chronic_disease: String?,
    @SerializedName("PrevSurgery")
    val prevSurgery: String?,
    @SerializedName("getAnswers")
    val answers: List<GetAnswers?>?,
)
data class GetAnswers(
    @SerializedName("DoctorID")
    val doctorID: Int?,
    @SerializedName("ClinicID")
    val clinicID: Int?,
    @SerializedName("AnswerDate")
    val AnswerDate: String?,
    @SerializedName("Answer")
    val answer: String?,
    @SerializedName("DoctorName")
    val doctorName: String?,
): Serializable