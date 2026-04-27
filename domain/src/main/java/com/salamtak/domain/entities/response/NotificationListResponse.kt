package com.salamtak.domain.entities.response


import com.google.gson.annotations.SerializedName

data class NotificationListResponse(
    @SerializedName("Data")
    val data: List<NotificationList>,
    @SerializedName("Message")
    val message: String,
    @SerializedName("MessageCode")
    val messageCode: Int,
    @SerializedName("Success")
    val success: Boolean
)

data class NotificationList(
    @SerializedName("Id")
    val id: Int,
    @SerializedName("IsSeen")
    val isSeen: Boolean,
    @SerializedName("Message")
    val message: String,
    @SerializedName("SentDate")
    val sentDate: String,
    @SerializedName("Title")
    val title: String,
    @SerializedName("UserID")
    val userID: Int
)