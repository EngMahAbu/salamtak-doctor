package com.salamtak.domain.entities.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class SubSpecialitiesResponse(
    val Data: List<SubSpecialitiesData>,
    val Message: String,
    val MessageCode: Int,
    val Success: Boolean
)

data class SubSpecialitiesData(
    val Id: Int,
    val Inactive: Boolean,
    val Name: String,
    val ParentId: Int,
    val SpecialistParentName: Int
)