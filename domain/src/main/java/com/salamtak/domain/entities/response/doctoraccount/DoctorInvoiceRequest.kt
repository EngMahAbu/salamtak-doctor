package com.salamtak.domain.entities.response.doctoraccount


import com.google.gson.annotations.SerializedName

data class DoctorInvoiceRequest(
    @SerializedName("Date")
    val date: String?,
    @SerializedName("DoctorId")
    val doctorId: Int?,
    @SerializedName("Items")
    val items: List<Item?>?,
    @SerializedName("PatientId")
    val patientId: Long?,
)
data class Item(
    @SerializedName("Date")
    val date: String?,
    @SerializedName("Paid")
    val paid: Int?,
    @SerializedName("Service")
    val service: String?,
    @SerializedName("ServiceFee")
    val serviceFee: Int?
)