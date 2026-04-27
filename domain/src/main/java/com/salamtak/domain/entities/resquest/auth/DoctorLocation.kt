package com.salamtak.domain.entities.resquest.auth

import java.io.Serializable

open class DoctorLocation{

    data class Location(
        val city: String,
        val country: String,
        val details: String,
        val district: String,
        val latitude: Double,
        val longitude: Double
    ): Serializable

}
