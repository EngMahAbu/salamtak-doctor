package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.clinic

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salamtak.core.newNetwork.ErrorModel
import com.salamtak.core.newNetwork.NetworkResponse
import com.salamtak.domain.entities.response.*
import kotlinx.coroutines.launch

class NewClinicViewModel : ViewModel() {


    val repo = NewClinicRepository()
    var showLoading = MutableLiveData<Boolean>()
    var clinicInfoModel = MutableLiveData<ClinicInfoResponse>()
    val error = MutableLiveData<ErrorModel>()

    fun getClinicInfo(clinicId: Int) {

        showLoading.value = true
        viewModelScope.launch {
            val clinicInfo = repo.getClinicInfo(clinicId)

            when (clinicInfo) {
                is NetworkResponse.Success -> {
                    clinicInfoModel.value = clinicInfo.body
                    Log.d("clinicInfo", "getClinicInfo: ${clinicInfo.body}")
                }
                is NetworkResponse.ApiError -> {
                    error.value = clinicInfo.body
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