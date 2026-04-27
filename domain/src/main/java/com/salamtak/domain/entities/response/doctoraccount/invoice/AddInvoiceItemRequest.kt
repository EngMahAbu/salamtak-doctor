package com.salamtak.domain.entities.response.doctoraccount.invoice


import com.google.gson.annotations.SerializedName

data class AddInvoiceItemRequest(
    @SerializedName("InvoiceId")
    val invoiceId: Int?,
    @SerializedName("Items")
    val items: List<ItemRequest?>?
)
data class ItemRequest(
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