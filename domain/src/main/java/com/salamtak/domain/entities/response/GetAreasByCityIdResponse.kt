package com.salamtak.domain.entities.response


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class GetAreasByCityIdResponse(
    @SerializedName("Data")
    val `data`: List<AreaData>
): Serializable

data class AreaData(
    @SerializedName("Id")
    val id: Int,
    @SerializedName("Name")
    val name: String
):Serializable