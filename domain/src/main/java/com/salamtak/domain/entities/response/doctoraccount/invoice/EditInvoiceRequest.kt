package com.salamtak.domain.entities.response.doctoraccount.invoice


import com.google.gson.annotations.SerializedName

data class EditInvoiceRequest(
    @SerializedName("Date")
    val date: String?,
    @SerializedName("InvoiceItemId")
    val invoiceItemId: Int?,
    @SerializedName("Paid")
    val paid: Int?,
    @SerializedName("Service")
    val service: String?,
    @SerializedName("ServiceFee")
    val serviceFee: Int?
)