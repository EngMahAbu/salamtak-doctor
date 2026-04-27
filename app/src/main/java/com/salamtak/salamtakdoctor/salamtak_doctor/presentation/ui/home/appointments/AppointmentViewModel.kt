package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.appointments

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salamtak.core.newNetwork.ErrorModel
import com.salamtak.core.newNetwork.NetworkResponse
import com.salamtak.domain.entities.response.AgoraMeetingResponse
import com.salamtak.domain.entities.response.AppointmentResponse
import kotlinx.coroutines.launch

class AppointmentViewModel : ViewModel() {

    val repo = AppointmentRepository()
    var showLoading = MutableLiveData<Boolean>()
    var AppointmentModel = MutableLiveData<AppointmentResponse>()
    var AgoraModel = MutableLiveData<AppointmentResponse>()
    var AgoraMeetingModel = MutableLiveData<AgoraMeetingResponse>()
    val error = MutableLiveData<ErrorModel>()
    var cancel = MutableLiveData<ErrorModel>()
    var changeAppointmentStatusResponse = MutableLiveData<ErrorModel>()
//    var sendCodee = MutableLiveData<RegisterResponse>()


    fun getAgoraMeeting(appointmentId: Int){
        showLoading.value = true
        viewModelScope.launch {
            val agoraAppRes = repo.getAgoraMeeting(appointmentId )

            when (agoraAppRes) {
                is NetworkResponse.Success -> {
                    AgoraMeetingModel.value = agoraAppRes.body!!
                    Log.d("AgoraMeetingModel", "AgoraMeetingModel: ${agoraAppRes.body}")
                }
                is NetworkResponse.ApiError -> {
                    error.value = agoraAppRes.body
                }
                is NetworkResponse.NetworkError -> {
                }
                is NetworkResponse.UnknownError -> {
                }
            }
            showLoading.value = false
        }
    }
    fun getAgoraAppId(){
        showLoading.value = true
        viewModelScope.launch {
            val agoraAppRes = repo.getAgoraAppId()

            when (agoraAppRes) {
                is NetworkResponse.Success -> {
                    AgoraModel.value = agoraAppRes.body
                    Log.d("getAgoraId", "getAgoraId: ${agoraAppRes.body}")
                }
                is NetworkResponse.ApiError -> {
                    error.value = agoraAppRes.body
                }
                is NetworkResponse.NetworkError -> {
                }
                is NetworkResponse.UnknownError -> {
                }
            }
            showLoading.value = false
        }

    }

    fun getAppointment(
        fromDate: String? = null,
        toDate: String? = null,
        clinicID: Int? = null,
        medicalTypeId: Int? = null,
        skipCount: Int? = null,
        maxResultCount: Int? = null,
        polyInfoID: Int? = null,
        doctorID: Int? = null,
    ) {
        showLoading.value = true
        viewModelScope.launch {

            val currentAppointment = if (polyInfoID==null){
                repo.getCurrentAppointment(
                    fromDate,
                    toDate,
                    clinicID,
                    medicalTypeId,
                    skipCount,
                    maxResultCount
                )
            }else{
                repo.getCurrentDoctorAppointmentPolyclinic(
                    fromDate,
                    toDate,
                    clinicID,
                    medicalTypeId,
                    skipCount,
                    maxResultCount,polyInfoID,doctorID
                )
            }
            when (currentAppointment) {
                is NetworkResponse.Success -> {
                    AppointmentModel.value = currentAppointment.body
                    Log.d("getAppointment", "getAppointment: ${currentAppointment.body}")
                }
                is NetworkResponse.ApiError -> {
                    error.value = currentAppointment.body
                }
                is NetworkResponse.NetworkError -> {
                }
                is NetworkResponse.UnknownError -> {
                }
            }
            showLoading.value = false
        }
    }

    fun getHistoryAppointment() {
        showLoading.value = true
        viewModelScope.launch {
            val historyAppointment = repo.getHistoryAppointment()

            when (historyAppointment) {
                is NetworkResponse.Success -> {
                    AppointmentModel.value = historyAppointment.body
                }
                is NetworkResponse.ApiError -> {
                    error.value = historyAppointment.body
                }
                is NetworkResponse.NetworkError -> {
                }
                is NetworkResponse.UnknownError -> {
                }
            }
            showLoading.value = false
        }
    }

    fun cancelAppointment(appointmentId: Int, cancelReason: String?=null) {
        showLoading.value = true
        viewModelScope.launch {
            val cancelAppointment = repo.cancelAppointment(appointmentId, cancelReason)

            when (cancelAppointment) {
                is NetworkResponse.Success -> {
                    cancel.value = cancelAppointment.body
                }
                is NetworkResponse.ApiError -> {
                    error.value = cancelAppointment.body
                }
                is NetworkResponse.NetworkError -> {
                }
                is NetworkResponse.UnknownError -> {
                }
            }
            showLoading.value = false
        }
    }

    fun changeAppointmentStatus(appointmentId: Int, statusId: Int?=null) {
//        showLoading.value = true
        viewModelScope.launch {
            val changeAppointmentStatus = repo.changeAppointmentStatus(appointmentId, statusId)

            when (changeAppointmentStatus) {
                is NetworkResponse.Success -> {
                    changeAppointmentStatusResponse.value = changeAppointmentStatus.body
                }
                is NetworkResponse.ApiError -> {
                    error.value = changeAppointmentStatus.body
                }
                is NetworkResponse.NetworkError -> {
                }
                is NetworkResponse.UnknownError -> {
                }
            }
//            showLoading.value = false
        }
    }

//    fun sendCodeAgain(sendCodeAgainModel: SendCodeAgainModel) {
//        showLoading.value = true
//        viewModelScope.launch {
//            val sendCode = repo.sendCodeAgain(sendCodeAgainModel)
//
//            when (sendCode) {
//                is NetworkResponse.Success -> {
//                    sendCodee.value = sendCode.body
//                }
//                is NetworkResponse.ApiError -> {
//                    error.value = sendCode.body
//                }
//                is NetworkResponse.NetworkError -> {
//                }
//                is NetworkResponse.UnknownError -> {
//                }
//            }
//            showLoading.value = false
//        }
//    }

}