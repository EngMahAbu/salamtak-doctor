package com.salamtak.domain.entities


import com.google.gson.annotations.SerializedName

data class AnswersDoctorResponse(
    @SerializedName("Data")
    val `data`: List<QuestionsAnswer?>?,
    @SerializedName("Message")
    val message: String?,
    @SerializedName("MessageCode")
    val messageCode: Int?,
    @SerializedName("Success")
    val success: Boolean?
)
data class QuestionsAnswer(
    @SerializedName("Age")
    val age: Int?,
    @SerializedName("Chronic_disease")
    val chronicDisease: String?,
    @SerializedName("GenderID")
    val genderID: Int?,
    @SerializedName("getAnswers")
    val getAnswers: List<GetAnswer?>?,
    @SerializedName("getAnswry")
    val getAnswry: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("IsForMe")
    val isForMe: Boolean?,
    @SerializedName("PatientFirstname")
    val patientFirstname: String?,
    @SerializedName("PatientFulltname")
    val patientFulltname: String?,
    @SerializedName("PatientID")
    val patientID: Int?,
    @SerializedName("PrevSurgery")
    val prevSurgery: String?,
    @SerializedName("PrevSurulltname")
    val prevSurulltname: String?,
    @SerializedName("QDate")
    val qDate: String?,
    @SerializedName("Question")
    val question: String?,
    @SerializedName("SpecialistID")
    val specialistID: Int?,
    @SerializedName("SpecialityName")
    val specialityName: String?
)
data class GetAnswer(
    @SerializedName("Answer")
    val answer: String?,
    @SerializedName("AnswerDate")
    val answerDate: String?,
    @SerializedName("ClinicID")
    val clinicID: Int?,
    @SerializedName("DoctorID")
    val doctorID: Int?,
    @SerializedName("DoctorName")
    val doctorName: String?
)