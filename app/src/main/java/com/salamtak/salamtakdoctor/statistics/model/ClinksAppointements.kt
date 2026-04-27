package com.salamtak.salamtakdoctor.statistics.model

import java.io.Serializable

data class ClinksAppointements(

    val currentPackageInclinicPatient:String?=null,
    val currentCompleteInclinicPatientCount:String?=null,
    val lastPackageInclinicPatient:String?=null,
    val lastCompleteInclinicPatientCount:String?=null,
    val inclinicAppointments:List<PacakgesDetails>
): Serializable
