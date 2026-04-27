package com.salamtak.domain.entities.resquest.auth

data class PatientAccountModel(
    var Date: String?=null,
    var DoctorName: String?=null,
    var Id: Int?=null,
    var Isdeleted: Boolean?=null,
    var Paid: Int?=null,
    var PatientId: Int?=null,
    var Service: String?=null,
    var ServiceFee: Int?=null
)