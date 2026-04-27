package com.salamtak.domain.entities.resquest.auth

data class ForgetPasswordModel(
    var Email: String? = null,
    var UserTypeId: Int? = null,
    var Phone: String? = null,
    var ResetMethod: Int? = null
)