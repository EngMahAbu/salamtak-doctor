package com.salamtak.domain.entities.resquest.auth

data class CreateUserModel(val phone: String, val email: String, val password: String, val name: String, val userTypeId: Int)