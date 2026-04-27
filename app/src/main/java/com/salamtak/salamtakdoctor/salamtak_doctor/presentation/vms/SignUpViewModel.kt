package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms

import androidx.lifecycle.MutableLiveData
import com.salamtak.core.usecase.auth.CreateUserUseCase
import com.salamtak.core.usecase.auth.RegisterUseCase
import com.salamtak.core.utils.SessionManagement
import com.salamtak.domain.entities.response.RegisterResponse
import com.salamtak.domain.entities.response.UserLoginResponse
import com.salamtak.domain.entities.resquest.auth.RegisterModel
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.DateUtils
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow


class SignUpViewModel(
    private val sessionManagement: SessionManagement,
    private val registerUseCase: RegisterUseCase,
    private val createUserUseCase: CreateUserUseCase
    ): BaseViewModel() {

    private val _signUpResponse: MutableLiveData<RegisterResponse> = MutableLiveData()
    var signUpResponse = _signUpResponse

    private val _createUserResponse: MutableLiveData<UserLoginResponse> = MutableLiveData()
    var createUserResponse = _createUserResponse

    private val phoneVerificationEventChannel = Channel<PhoneVerificationEvent>()
    val phoneVerificationEvent = phoneVerificationEventChannel.receiveAsFlow()

    fun signUp(registerModel: RegisterModel){

        callApi(_signUpResponse) { registerUseCase(registerModel,
            DateUtils.getLang()!!
            ) }
    }

    fun createUser(registerModel: RegisterModel){

        callApi(_createUserResponse) { createUserUseCase(registerModel,
            DateUtils.getLang()!!
            ) }
    }

    sealed class PhoneVerificationEvent {
        object VerificationLoading : PhoneVerificationEvent()
        data class VerificationFailed(val message: String) : PhoneVerificationEvent()
        data class VerificationSuccess(val code: Int) : PhoneVerificationEvent()
    }

}