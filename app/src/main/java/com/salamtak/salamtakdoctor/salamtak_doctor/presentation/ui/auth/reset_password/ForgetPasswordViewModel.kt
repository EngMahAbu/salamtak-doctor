package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.auth.reset_password

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salamtak.core.newNetwork.ErrorModel
import com.salamtak.core.newNetwork.NetworkResponse
import com.salamtak.core.utils.SingleLiveEvent
import com.salamtak.domain.entities.response.ResetPasswordResponse
import com.salamtak.domain.entities.resquest.auth.ForgetPasswordModel
import com.salamtak.domain.entities.resquest.auth.NewPasswordModel
import kotlinx.coroutines.launch

class ForgetPasswordViewModel : ViewModel() {

    val repo = ForgetPasswordRepository()
    var showLoading = MutableLiveData<Boolean>()
    var forgetPasswordModel = SingleLiveEvent<ResetPasswordResponse>()
    val error = MutableLiveData<ErrorModel>()

    fun forgetPassword(forgetPasswoordModel : ForgetPasswordModel) {
        showLoading.value = true
        viewModelScope.launch {
            val forgetPassword = repo.forgetPassword(forgetPasswoordModel)

            when (forgetPassword) {
                is NetworkResponse.Success -> {
                    forgetPasswordModel.value = forgetPassword.body
                }
                is NetworkResponse.ApiError -> {
                    error.value = forgetPassword.body
                }
                is NetworkResponse.NetworkError -> {
                }
                is NetworkResponse.UnknownError -> {
                }
            }
            showLoading.value = false
        }
    }

    fun newPassword(newPasswordModel : NewPasswordModel) {
        showLoading.value = true
        viewModelScope.launch {
            val newPassword = repo.newPassword(newPasswordModel)

            when (newPassword) {
                is NetworkResponse.Success -> {
                    forgetPasswordModel.value = newPassword.body
                }
                is NetworkResponse.ApiError -> {
                    error.value = newPassword.body
                }
                is NetworkResponse.NetworkError -> {
                }
                is NetworkResponse.UnknownError -> {
                }
            }
            showLoading.value = false
        }
    }

    fun firstNewPassword(newPasswordModel : NewPasswordModel) {
        showLoading.value = true
        viewModelScope.launch {
            val newPassword = repo.firstNewPassword(newPasswordModel)

            when (newPassword) {
                is NetworkResponse.Success -> {
                    forgetPasswordModel.value = newPassword.body
                }
                is NetworkResponse.ApiError -> {
                    error.value = newPassword.body
                }
                is NetworkResponse.NetworkError -> {
                }
                is NetworkResponse.UnknownError -> {
                }
            }
            showLoading.value = false
        }
    }

}