package com.salamtak.domain.entities.resquest.auth

data class NewPasswordModel(
    var Password: String? = null,
    var UserId: Int? = null,
    var OldPassword: String? = null
)