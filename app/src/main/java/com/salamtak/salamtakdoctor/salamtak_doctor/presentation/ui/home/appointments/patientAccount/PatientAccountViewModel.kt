package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.appointments.patientAccount

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salamtak.core.newNetwork.ErrorModel
import com.salamtak.core.newNetwork.NetworkResponse
import com.salamtak.domain.entities.response.GetPatientAccountResponse
import com.salamtak.domain.entities.response.PatientAccountResponse
import com.salamtak.domain.entities.resquest.auth.PatientAccountModel
import kotlinx.coroutines.launch
import kotlin.math.log

class PatientAccountViewModel : ViewModel() {

    val repo = PatientAccountRepository()
    var showLoading = MutableLiveData<Boolean>()
    var patientAccountModel = MutableLiveData<PatientAccountResponse>()
    var getPatientAccountResponse = MutableLiveData<GetPatientAccountResponse>()
    var needRefresh = MutableLiveData<Boolean>()
    val error = MutableLiveData<ErrorModel>()

    fun getPatientAccount(patientId: Int) {

        showLoading.value = true
        viewModelScope.launch {
            val patientAccount = repo.patientAccount(patientId)

            when (patientAccount) {
                is NetworkResponse.Success -> {
                    getPatientAccountResponse.value = patientAccount.body
                }
                is NetworkResponse.ApiError -> {
                    error.value = patientAccount.body
                }
                is NetworkResponse.NetworkError -> {
                }
                is NetworkResponse.UnknownError -> {
                }
            }
            showLoading.value = false
        }
    }

    fun addPatientAccount(patientAccountsModel: PatientAccountModel) {

        showLoading.value = true
        viewModelScope.launch {
            val addpatientAccount = repo.addPatientAccount(patientAccountsModel)

            when (addpatientAccount) {
                is NetworkResponse.Success -> {
//                    patientAccountModel.value = addpatientAccount.body
                    needRefresh.value = true
                    Log.d("getData", "addPatientAccount: value is ${needRefresh.value}")
                }
                is NetworkResponse.ApiError -> {
                    if (addpatientAccount.body != null)
                        error.value = addpatientAccount.body
                }
                is NetworkResponse.NetworkError -> {
                }
                is NetworkResponse.UnknownError -> {
                }
            }
            showLoading.value = false
        }
    }



    fun editPatientAccount(patientAccountsModel: PatientAccountModel) {

        showLoading.value = true
        viewModelScope.launch {
            val addpatientAccount = repo.editPatientAccount(patientAccountsModel)

            when (addpatientAccount) {
                is NetworkResponse.Success -> {
//                    patientAccountModel.value = addpatientAccount.body
                    needRefresh.value = true
                }
                is NetworkResponse.ApiError -> {
                    error.value = addpatientAccount.body
                }
                is NetworkResponse.NetworkError -> {
                }
                is NetworkResponse.UnknownError -> {
                }
            }
            showLoading.value = false
        }
    }

    fun deletePatientAccount(dataId: Int) {

        showLoading.value = true
        viewModelScope.launch {
            val deletePatientAccount = repo.deletePatientAccount(dataId)

            when (deletePatientAccount) {
                is NetworkResponse.Success -> {
                    error.value = deletePatientAccount.body
                }
                is NetworkResponse.ApiError -> {
                    error.value = deletePatientAccount.body
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