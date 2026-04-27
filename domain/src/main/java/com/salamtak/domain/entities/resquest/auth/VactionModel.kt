package com.salamtak.domain.entities.resquest.auth

data class VactionModel(
    var ClinicID: Int? = null,
    var DoctorID: Int? = null,
    var FromDate: String? = null,
    var ToDate: String? = null,
    var VacationName: String? = null,
    var id: Int? = null
)