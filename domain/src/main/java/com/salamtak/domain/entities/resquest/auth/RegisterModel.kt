package com.salamtak.domain.entities.resquest.auth

data class RegisterModel(val phone: String?=null,val email: String?=null, val password: String?=null,val name: String?=null, val userTypeId: Int?=null)