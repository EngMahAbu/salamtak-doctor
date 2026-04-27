package com.salamtak.domain.entities.response.symtemos


import com.google.gson.annotations.SerializedName

data class SyemtomsResponse(
    @SerializedName("Data")
    val `data`: Data?,
    @SerializedName("Message")
    val message: String?,
    @SerializedName("MessageCode")
    val messageCode: Int?,
    @SerializedName("Success")
    val success: Boolean?
)
data class Data(
    @SerializedName("Age")
    val age: Int?,
    @SerializedName("Comment")
    val comment: String?,
    @SerializedName("DoctorResponseText")
    val doctorResponseText: String?,
    @SerializedName("DoctorResponded")
    val doctorResponded: Boolean?,
    @SerializedName("PatientId")
    val patientId: Int?,
    @SerializedName("PatientName")
    val patientName: String?,
    @SerializedName("PatientSymptomId")
    val patientSymptomId: Int?,
    @SerializedName("Symptoms")
    val symptoms: List<Symptom?>?
)
data class Symptom(
    @SerializedName("Id")
    val id: Int?,
    @SerializedName("Name")
    val name: String?
)