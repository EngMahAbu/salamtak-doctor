package com.salamtak.salamtakdoctor.model

data class PatientProfile(
    val name:String,
    val phone:String,
    val clinicName:String,
    val clinicAddress:String,
    val clinicPhone:List<String>,
)
