package com.salamtak.domain.entities.response

import com.google.gson.annotations.SerializedName

data class EmrAttachmentResponse(
    val Data: AttachmentEmrData,
    val Message: String,
    val MessageCode: Int,
    val Success: Boolean
)

data class AttachmentEmrData(
    val DocumentUrl: String,
    val Id: Int
)