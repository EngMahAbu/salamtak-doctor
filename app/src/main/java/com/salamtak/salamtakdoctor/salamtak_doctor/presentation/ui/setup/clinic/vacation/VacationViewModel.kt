package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.setup.clinic.vacation

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.salamtak.core.newNetwork.ErrorModel
import com.salamtak.core.newNetwork.NetworkResponse
import com.salamtak.domain.entities.response.VactionResponse
import com.salamtak.domain.entities.resquest.auth.VactionModel
import kotlinx.coroutines.launch

class VacationViewModel : ViewModel() {

    val repo = VacationRepository()
    var showLoading = MutableLiveData<Boolean>()
    var vactionModel = MutableLiveData<VactionResponse>()
    val error = MutableLiveData<ErrorModel>()

    fun getVaction(clinicId: Int) {

        showLoading.value = true
        viewModelScope.launch {
            val vaction = repo.vaction(clinicId)

            if (vaction != null){
                when (vaction) {
                    is NetworkResponse.Success -> {
                        vactionModel.value = vaction.body
                    }
                    is NetworkResponse.ApiError -> {
                        error.value = vaction.body
                    }
                    is NetworkResponse.NetworkError -> {
                    }
                    is NetworkResponse.UnknownError -> {
                    }
                }
            }
            showLoading.value = false
        }
    }

    fun addVaction(vactionsModel: VactionModel) {

        showLoading.value = true
        viewModelScope.launch {
            val addVactions = repo.addVaction(vactionsModel)

            when (addVactions) {
                is NetworkResponse.Success -> {
                    vactionModel.value = addVactions.body
                }
                is NetworkResponse.ApiError -> {
                    error.value = addVactions.body
                }
                is NetworkResponse.NetworkError -> {
                }
                is NetworkResponse.UnknownError -> {
                }
            }
            showLoading.value = false
        }
    }

    fun editVaction(vactionsModel: VactionModel) {

        val gson = Gson()
        val json = gson.toJson(vactionsModel)
        Log.d("getData", "editVaction: $json")
        showLoading.value = true
        viewModelScope.launch {
            val editVactions = repo.editVaction(vactionsModel)

           if (editVactions != null){
               when (editVactions) {
                   is NetworkResponse.Success -> {
                       vactionModel.value = editVactions.body
                       Log.d("editVactaion", "editVaction: ${editVactions.body}")
                   }
                   is NetworkResponse.ApiError -> {
                       error.value = editVactions.body
                   }
                   is NetworkResponse.NetworkError -> {
                   }
                   is NetworkResponse.UnknownError -> {
                   }
               }
           }
            showLoading.value = false
        }
    }

    fun deleteVaction(doctorVacationId: Int) {

        showLoading.value = true
        viewModelScope.launch {
            val deleteVaction = repo.deleteVaction(doctorVacationId)

            when (deleteVaction) {
                is NetworkResponse.Success -> {
                    error.value = deleteVaction.body

                }
                is NetworkResponse.ApiError -> {
                    error.value = deleteVaction.body
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