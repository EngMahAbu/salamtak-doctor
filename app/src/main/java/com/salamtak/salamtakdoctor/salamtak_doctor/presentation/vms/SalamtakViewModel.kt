package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.vms

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salamtak.core.newNetwork.ErrorModel
import com.salamtak.core.newNetwork.NetworkResponse
import com.salamtak.domain.entities.response.*
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.auth.login.newLogIn.NewLoginRepository
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.TAG
import kotlinx.coroutines.launch

class SalamtakViewModel : ViewModel() {

    val Repo = NewLoginRepository()
    var showLoading = MutableLiveData<Boolean>()
    var citiesResponse = MutableLiveData<GetAllCitiesResponse>()
    var areaResponse = MutableLiveData<GetAreasByCityIdResponse>()
    var deleteResponse = MutableLiveData<DeleteUserResponse>()
    var deleteSchduleResponse = MutableLiveData<DeleteScheduleResponse>()
    val error = MutableLiveData<ErrorModel>()

    fun getAllCities(){
        showLoading.value = true
        viewModelScope.launch {
            val city = Repo.getAllCities()

            when (city) {
                is NetworkResponse.Success -> {
                    citiesResponse.value=city.body
                    Log.d(TAG, "network mansour: " + city.body)
                }
                is NetworkResponse.ApiError -> {
                    Log.d(TAG, "network mansour: " + city.code)
                    error.value = city.body
                }
                is NetworkResponse.NetworkError -> {
                    Log.d(TAG, "network mansour: " + city.error)
                }
                is NetworkResponse.UnknownError -> {
                    Log.d(TAG, "network mansour: " + city.error)
                }
            }
            showLoading.value = false
        }
    }

    fun getAllAreas(cityId: Int){
        showLoading.value = true
        viewModelScope.launch {
            val city = Repo.getAreasByCityId(cityId)

            when (city) {
                is NetworkResponse.Success -> {
                    areaResponse.value=city.body
                    Log.d(TAG, "network mansour: " + city.body)
                }
                is NetworkResponse.ApiError -> {
                    Log.d(TAG, "network mansour: " + city.code)
                    error.value = city.body
                }
                is NetworkResponse.NetworkError -> {
                    Log.d(TAG, "network mansour: " + city.error)
                }
                is NetworkResponse.UnknownError -> {
                    Log.d(TAG, "network mansour: " + city.error)
                }
            }
            showLoading.value = false
        }
    }

    fun deleteUser(){
        showLoading.value = true
        viewModelScope.launch {
            val del = Repo.deleteUser()

            when (del) {
                is NetworkResponse.Success -> {
                    deleteResponse.value=del.body
                    Log.d(TAG, "network mansour: " + del.body)
                }
                is NetworkResponse.ApiError -> {
                    Log.d(TAG, "network mansour: " + del.code)
                    error.value = del.body
                }
                is NetworkResponse.NetworkError -> {
                    Log.d(TAG, "network mansour: " + del.error)
                }
                is NetworkResponse.UnknownError -> {
                    Log.d(TAG, "network mansour: " + del.error)
                }
            }
            showLoading.value = false
        }
    }

    fun deleteDoctorClinicSchedual(deleteSchduleRequest: DeleteSchduleRequest){
        showLoading.value = true
        viewModelScope.launch {
            val del = Repo.deleteDoctorClinicSchedual(deleteSchduleRequest)

            when (del) {
                is NetworkResponse.Success -> {
                    deleteSchduleResponse.value=del.body
                    Log.d(TAG, "network mansour: " + del.body)
                }
                is NetworkResponse.ApiError -> {
                    Log.d(TAG, "network mansour: " + del.code)
                    error.value = del.body
                }
                is NetworkResponse.NetworkError -> {
                    Log.d(TAG, "network mansour: " + del.error)
                }
                is NetworkResponse.UnknownError -> {
                    Log.d(TAG, "network mansour: " + del.error)
                }
            }
            showLoading.value = false
        }
    }

}