package com.salamtak.domain.entities.response.emr


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("AppointmentId")
    val appointmentId: Int?,
    @SerializedName("AppountmentDate")
    val appountmentDate: String?,
    @SerializedName("ClinicAddress")
    val clinicAddress: String?,
    @SerializedName("ClinicName")
    val clinicName: String?,
    @SerializedName("ClinicPhone")
    val clinicPhone: List<String?>?,
    @SerializedName("DoctorName")
    val doctorName: String?,
    @SerializedName("DoctorSpeciality")
    val doctorSpeciality: String?,
    @SerializedName("EmrId")
    val emrId: Int?,
    @SerializedName("Instructions")
    val instructions: String?,
    @SerializedName("MedicalExaminationTypeId")
    val medicalExaminationTypeId: Int?,
    @SerializedName("MedicalExaminationTypeName")
    val medicalExaminationTypeName: String?,
    @SerializedName("PatientEmrdetails")
    val patientEmrdetails: List<Any?>?,
    @SerializedName("PatientEmrdocuments")
    val patientEmrdocuments: List<Any?>?,
    @SerializedName("PatientName")
    val patientName: String?,
    @SerializedName("PatientPhone")
    val patientPhone: String?
)