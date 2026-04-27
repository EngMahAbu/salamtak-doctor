package com.salamtak.domain.entities.resquest.auth

data class LocationModel( val token: String,
                          val Street: String,
                          val ClinicId: Int,
                          val CityId: Int,
                          val AreaId: Int,
                          val Latitude: String,
                          val FloorNo: Int,
                          val BlockNo: String,
                          val Longitude: String,
                          val CountryId: Int,
                          val ApartmentNo: String,
                          val Address: String,
                          val CityName: String,
                          val AreaName: String,
                          )
