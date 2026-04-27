package com.salamtak.salamtakdoctor.salamtakgates

import java.io.Serializable

data class GatesDetails(
    val gatesId: Int?,
    val title: String?,
    val description: String?,
    val image: String?,
    val date: String?,
    val links: List<String>?
):Serializable
