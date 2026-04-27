package com.salamtak.domain.entities.resquest.auth

data class CreateSpecialistModel(
    val token: String,
    val specialistModel: SpecialistModel
    )

data class SpecialistModel(
    val Name: String,
    val NameAr: String,
    val ParentId: Int,
    val Inactive: Boolean
)