package com.salamtak.domain.entities.response.doctoraccount.invoice


import com.google.gson.annotations.SerializedName

data class DoctorInvoiceResponse(

    @SerializedName("Data")
    val `data`: Data?,
    @SerializedName("Message")
    val message: String?,
    @SerializedName("MessageCode")
    val messageCode: Int?,
    @SerializedName("Success")
    val success: Boolean?,
)
data class Data(
    @SerializedName("Data")
    val `data`: List<PatientData>?,
    @SerializedName("TotalPaid")
    val totalPaid: Int?,
    @SerializedName("TotalServiceFee")
    val totalServiceFee: Int?
)
data class PatientData(
    @SerializedName("Date")
    val date: String?,
    @SerializedName("DoctorId")
    val doctorId: Int?,
    @SerializedName("Id")
    val id: Int?,
    @SerializedName("Items")
    val items: List<Item?>?,
    @SerializedName("PaidAmount")
    val paidAmount: Int?,
    @SerializedName("PatientId")
    val patientId: Int?,
    @SerializedName("PatientName")
    val patientName: String?,
    @SerializedName("TotalAmount")
    val totalAmount: Int?
)
data class Item(
    @SerializedName("Date")
    val date: String?,
    @SerializedName("Id")
    val id: Int?,
    @SerializedName("InvoiceId")
    val invoiceId: Int?,
    @SerializedName("Paid")
    val paid: Int?,
    @SerializedName("Service")
    val service: String?,
    @SerializedName("ServiceFee")
    val serviceFee: Int?
)