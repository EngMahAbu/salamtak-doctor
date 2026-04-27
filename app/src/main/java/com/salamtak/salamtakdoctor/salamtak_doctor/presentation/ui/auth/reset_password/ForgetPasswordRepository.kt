package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.auth.reset_password

import com.salamtak.core.localDataBase.Localization
import com.salamtak.core.newNetwork.RetrofitBuilder
import com.salamtak.domain.entities.resquest.auth.ForgetPasswordModel
import com.salamtak.domain.entities.resquest.auth.NewPasswordModel

class ForgetPasswordRepository {

    private val client = RetrofitBuilder().instant

    suspend fun forgetPassword(forgetPasswordModel : ForgetPasswordModel) =
        client.forgetPassword(Localization.getLanguageName(),forgetPasswordModel)

    suspend fun newPassword(newPasswordModel : NewPasswordModel) =
        client.newPassword(Localization.getLanguageName(),newPasswordModel)

    suspend fun firstNewPassword(newPasswordModel : NewPasswordModel) =
        client.firstNewPassword(Localization.getLanguageName(),newPasswordModel)

}