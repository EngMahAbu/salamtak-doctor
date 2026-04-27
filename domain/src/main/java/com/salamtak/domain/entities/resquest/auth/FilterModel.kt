package com.salamtak.domain.entities.resquest.auth

data class FilterModel(
    var ClinicId: Int?=null,
    var FromDate: String?=null,
    var MaxResultCount: Int?=null,
    var MedicalExaminationTypeId: Int?=null,
    var PatientName: String?=null,
    var SkipCount: Int?=null,
    var ToDate: String?=null
)