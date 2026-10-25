package com.salamtak.domain.entities.response.queue


import com.google.gson.annotations.SerializedName

data class QueueBoardResponse(
    @SerializedName("Data")
    val data: QueueBoardData?,
    @SerializedName("Message")
    val message: String?,
    @SerializedName("MessageCode")
    val messageCode: Int,
    @SerializedName("Success")
    val success: Boolean
)

data class QueueBoardData(
    @SerializedName("CurrentlyServingTicketId")
    val currentlyServingTicketId: Int?,
    @SerializedName("CurrentlyServingPosition")
    val currentlyServingPosition: Int?,
    @SerializedName("CurrentlyServingPatientName")
    val currentlyServingPatientName: String?,
    @SerializedName("WaitingList")
    val waitingList: List<QueueTicket>?
)

data class QueueTicket(
    @SerializedName("Id")
    val id: Int,
    @SerializedName("TicketPosition")
    val ticketPosition: Int,
    @SerializedName("PatientName")
    val patientName: String?,
    @SerializedName("Status")
    val status: Int,
    @SerializedName("StatusName")
    val statusName: String?,
    @SerializedName("IsWalkIn")
    val isWalkIn: Boolean,
    @SerializedName("OriginalBookingTime")
    val originalBookingTime: String?,
    @SerializedName("CheckInTime")
    val checkInTime: String?,
    @SerializedName("ServiceStartTime")
    val serviceStartTime: String?,
    @SerializedName("DoctorName")
    val doctorName: String?,
    @SerializedName("DepartmentName")
    val departmentName: String?,
    // Not in GetQueueBoard's ticket schema yet; shown when the backend sends it
    @SerializedName("EstimatedWaitMinutes")
    val estimatedWaitMinutes: Int?
)

data class QueueActionResponse(
    @SerializedName("Data")
    val data: QueueOperationStatus?,
    @SerializedName("Message")
    val message: String?,
    @SerializedName("MessageCode")
    val messageCode: Int,
    @SerializedName("Success")
    val success: Boolean
)

data class QueueOperationStatus(
    @SerializedName("Statues")
    val statues: Boolean
)

data class QueueTicketActionRequest(
    @SerializedName("TicketId")
    val ticketId: Int
)

data class CancelQueueTicketRequest(
    @SerializedName("TicketId")
    val ticketId: Int,
    @SerializedName("CancelReason")
    val cancelReason: String? = null
)
