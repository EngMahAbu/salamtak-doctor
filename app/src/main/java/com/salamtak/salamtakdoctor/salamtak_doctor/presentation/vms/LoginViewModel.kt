package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bumptech.glide.load.engine.Resource
import com.salamtak.core.network.ApiEndpoints
import com.salamtak.core.usecase.auth.LoginUseCase
import com.salamtak.core.utils.SessionManagement
import com.salamtak.domain.entities.response.UserLoginResponse
import com.salamtak.domain.entities.resquest.auth.LoginModel
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.DateUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LoginViewModel(
    private val sessionManagement: SessionManagement,
    private val loginUseCase: LoginUseCase
    ): BaseViewModel() {

    val TAGs = LoginViewModel::class.java.simpleName

    private val _loginResponse :MutableLiveData<UserLoginResponse> = MutableLiveData()
    var loginResponse = _loginResponse

    fun login(loginModel: LoginModel){

        callApi(_loginResponse){ loginUseCase(loginModel,
            DateUtils.getLang()!!
            )}

    }
}