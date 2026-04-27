package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.appointments.filter

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salamtak.core.newNetwork.ErrorModel
import com.salamtak.core.newNetwork.NetworkResponse
import com.salamtak.domain.UpdateMeetingResponse
import com.salamtak.domain.entities.response.AppointmentResponse
import com.salamtak.domain.entities.response.FilterResponse
import com.salamtak.domain.entities.response.MeetingInfoModel
import com.salamtak.domain.entities.resquest.auth.FilterModel
import kotlinx.coroutines.launch

class FilterViewModel :ViewModel() {

    val repo = FilterRepository()
    var showLoading = MutableLiveData<Boolean>()
    val error = MutableLiveData<ErrorModel>()
    val filterResponseModel = MutableLiveData<AppointmentResponse>()
//    val meetingInfoModel = MutableLiveData<MeetingInfoModel>()
    val updateMeetingResponse = MutableLiveData<UpdateMeetingResponse>()

    fun filter(filterModel: FilterModel) {
        showLoading.value = true
        viewModelScope.launch {
            val filter = repo.filter(filterModel)

            when (filter) {
                is NetworkResponse.Success -> {
                    filterResponseModel.value = filter.body
                }
                is NetworkResponse.ApiError -> {
                    error.value = filter.body
                }
                is NetworkResponse.NetworkError -> {
                }
                is NetworkResponse.UnknownError -> {
                }
            }
            showLoading.value = false
        }
    }

//    fun startMeeting( token:String,appointmentId:String) {
//        showLoading.value = true
//        viewModelScope.launch {
//            val meeting = repo.startMeeting(token, appointmentId)
//
//            when (meeting) {
//                is NetworkResponse.Success -> {
//                    meetingInfoModel.value = meeting.body
//                }
//                is NetworkResponse.ApiError -> {
//                    error.value = meeting.body
//                }
//                is NetworkResponse.NetworkError -> {
//                }
//                is NetworkResponse.UnknownError -> {
//                }
//            }
//            showLoading.value = false
//        }
//    }

//    fun updateMeeting( token:String,appointmentId:String) {
    fun updateMeeting(appointmentId:String) {
        showLoading.value = true
        viewModelScope.launch {
            val meeting = repo.updateMeeting(appointmentId)

            when (meeting) {
                is NetworkResponse.Success -> {
                    updateMeetingResponse.value = meeting.body
                }
                is NetworkResponse.ApiError -> {
                    error.value = meeting.body
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