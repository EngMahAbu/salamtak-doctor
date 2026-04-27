package com.salamtak.domain.entities.response


import com.google.gson.annotations.SerializedName

data class DoctorAccountResponse(
    @SerializedName("Data")
    val data: ToTalAccount?,
    @SerializedName("Message")
    val message: String?,
    @SerializedName("MessageCode")
    val messageCode: Int?,
    @SerializedName("Success")
    val success: Boolean?
)
data class ToTalAccount(
    @SerializedName("Data")
    val data: List<DataAccount>?,
    @SerializedName("TotalPaid")
    val totalPaid: Double?,
    @SerializedName("TotalServiceFee")
    val totalServiceFee: Double?
)

data class DataAccount(
    @SerializedName("Date")
    val date: String?,
    @SerializedName("Deleted")
    val deleted: Boolean?,
    @SerializedName("DoctorName")
    val doctorName: String?,
    @SerializedName("Id")
    val id: Int?,
    @SerializedName("Paid")
    var paid: Double?,
    @SerializedName("PatientFulltname")
    val patientFulltname: String?,
    @SerializedName("PatientId")
    val patientId: Int?,
    @SerializedName("Service")
    val service: String?,
    @SerializedName("ServiceFee")
    val serviceFee: Double?
)