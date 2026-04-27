package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.auth.login.newLogIn

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salamtak.core.newNetwork.ErrorModel
import com.salamtak.core.newNetwork.NetworkResponse
import com.salamtak.domain.entities.response.UserLoginResponse
import com.salamtak.domain.entities.resquest.auth.LoginModel
import kotlinx.coroutines.launch

class NewLoginViewModel : ViewModel() {

    val signInRepo = NewLoginRepository()
    var showLoading = MutableLiveData<Boolean>()
    var signInModel = MutableLiveData<UserLoginResponse>()
    val error = MutableLiveData<ErrorModel>()

    fun newSignIn(signInRequest: LoginModel) {
        showLoading.value = true
        viewModelScope.launch {
            val signIn = signInRepo.newSignIn(signInRequest)

            when (signIn) {
                is NetworkResponse.Success -> {
                    signInModel.value = signIn.body
                    Log.d(TAG, "network mansour: " + signIn.body)
                }
                is NetworkResponse.ApiError -> {
                    Log.d(TAG, "network mansour: " + signIn.code)
                    error.value = signIn.body
                }
                is NetworkResponse.NetworkError -> {
                    Log.d(TAG, "network mansour: " + signIn.error)
                }
                is NetworkResponse.UnknownError -> {
                    Log.d(TAG, "network mansour: " + signIn.error)
                }
            }
            showLoading.value = false
        }
    }

}