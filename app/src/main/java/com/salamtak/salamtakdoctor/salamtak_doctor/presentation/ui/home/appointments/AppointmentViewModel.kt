package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.home.appointments

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salamtak.core.newNetwork.ErrorModel
import com.salamtak.core.newNetwork.NetworkResponse
import com.salamtak.domain.entities.response.AgoraMeetingResponse
import com.salamtak.domain.entities.response.AppointmentResponse
import com.salamtak.domain.entities.response.Item
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

    // Paging for current appointments: each page is appended to loadedAppointments and
    // AppointmentModel always carries the whole list so far, so positions stay valid
    val loadedAppointments = ArrayList<Item>()
    var loadingMore = MutableLiveData(false)
    private var totalAppointments = 0
    private var lastQuery: AppointmentQuery? = null

    private data class AppointmentQuery(
        val fromDate: String?,
        val toDate: String?,
        val clinicID: Int?,
        val medicalTypeId: Int?,
        val pageSize: Int,
        val polyInfoID: Int?,
        val doctorID: Int?,
    )

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
        // A new search/filter starts paging over from the first page
        val query = AppointmentQuery(
            fromDate, toDate, clinicID, medicalTypeId,
            maxResultCount ?: 10, polyInfoID, doctorID
        )
        lastQuery = query
        loadingMore.value = false
        showLoading.value = true
        fetchAppointments(query, skipCount ?: 0, append = false)
    }

    fun hasMoreAppointments(): Boolean =
        lastQuery != null && loadingMore.value != true && showLoading.value != true &&
                loadedAppointments.size < totalAppointments

    fun loadMoreAppointments() {
        val query = lastQuery ?: return
        if (!hasMoreAppointments()) return
        loadingMore.value = true
        fetchAppointments(query, loadedAppointments.size, append = true)
    }

    private fun fetchAppointments(query: AppointmentQuery, skipCount: Int, append: Boolean) {
        viewModelScope.launch {

            val currentAppointment = if (query.polyInfoID==null){
                repo.getCurrentAppointment(
                    query.fromDate,
                    query.toDate,
                    query.clinicID,
                    query.medicalTypeId,
                    skipCount,
                    query.pageSize
                )
            }else{
                repo.getCurrentDoctorAppointmentPolyclinic(
                    query.fromDate,
                    query.toDate,
                    query.clinicID,
                    query.medicalTypeId,
                    skipCount,
                    query.pageSize,query.polyInfoID,query.doctorID
                )
            }
            // Ignore a late response for a search the user has already replaced
            if (query !== lastQuery) return@launch
            when (currentAppointment) {
                is NetworkResponse.Success -> {
                    val body = currentAppointment.body
                    if (!append) loadedAppointments.clear()
                    loadedAppointments.addAll(body.Data.Items.orEmpty())
                    totalAppointments = body.Data.TotalCount
                    AppointmentModel.value =
                        body.copy(Data = body.Data.copy(Items = loadedAppointments))
                    Log.d("getAppointment", "getAppointment: loaded ${loadedAppointments.size} of $totalAppointments")
                }
                is NetworkResponse.ApiError -> {
                    error.value = currentAppointment.body
                }
                is NetworkResponse.NetworkError -> {
                }
                is NetworkResponse.UnknownError -> {
                }
            }
            if (append) loadingMore.value = false else showLoading.value = false
        }
    }

    fun getHistoryAppointment() {
        // History isn't paged; stop paging the current list
        lastQuery = null
        loadingMore.value = false
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