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
import com.salamtak.domain.entities.response.NewsData
import com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.hospital.HospitalRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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

    // Hospital list (see getHospitalAppointments)
    private val hospitalRepo = HospitalRepository()
    private var hospitalRequest: Any? = null

    private data class AppointmentQuery(
        val fromDate: String?,
        val toDate: String?,
        val clinicID: Int?,
        val medicalTypeId: Int?,
        val pageSize: Int,
        val polyInfoID: Int?,
        val doctorID: Int?,
        val patientName: String?,
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
        patientName: String? = null,
    ) {
        // A new search/filter starts paging over from the first page
        val query = AppointmentQuery(
            fromDate, toDate, clinicID, medicalTypeId,
            maxResultCount ?: 10, polyInfoID, doctorID, patientName?.takeIf { it.isNotBlank() }
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
                    query.pageSize,query.polyInfoID,query.doctorID,query.patientName
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

    /**
     * Hospital list: GetCurrentDoctorAppointment only answers per doctor, so ask for [doctorId] or,
     * with "All Doctors", for every doctor of the clinic / hospital, and merge by date.
     * Not paged: each doctor's page is large enough for the current list.
     */
    fun getHospitalAppointments(
        hospitalId: Int,
        clinicId: Int?,
        doctorId: Int?,
        fromDate: String?,
        toDate: String?,
        medicalTypeId: Int?,
        patientName: String?,
    ) {
        // TODO(dummy-appointments): remove once the hospital has an appointments endpoint
        if (DummyAppointments.enabled) {
            // Hospital debug builds show static appointments (see DummyAppointments)
            showDummyAppointments(patientName)
            return
        }
        lastQuery = null
        loadingMore.value = false
        showLoading.value = true
        val request = Any()
        hospitalRequest = request
        viewModelScope.launch {
            val doctorIds = if (doctorId != null) listOf(doctorId) else runCatching {
                val response = if (clinicId != null) hospitalRepo.getClinicDoctors(clinicId)
                else hospitalRepo.getHospitalDoctorList(hospitalId)
                response.body()?.Data.orEmpty().map { it.DoctorId }.distinct()
            }.getOrDefault(emptyList())

            val results = doctorIds.map { id ->
                async {
                    repo.getHospitalDoctorAppointments(
                        id, fromDate, toDate, medicalTypeId?.takeIf { it != 0 },
                        patientName?.takeIf { it.isNotBlank() }, HOSPITAL_PAGE_SIZE
                    )
                }
            }.awaitAll()
            // Ignore a late response for a filter the user has already replaced
            if (request !== hospitalRequest) return@launch

            val items = results.filterIsInstance<NetworkResponse.Success<AppointmentResponse>>()
                .flatMap { it.body.Data.Items.orEmpty() }
                .sortedBy { it.AppointmentDate }
            loadedAppointments.clear()
            loadedAppointments.addAll(items)
            totalAppointments = items.size
            val first = results.firstOrNull { it is NetworkResponse.Success } as? NetworkResponse.Success
            if (first != null || doctorIds.isEmpty()) {
                val body = first?.body
                AppointmentModel.value = body?.copy(Data = body.Data.copy(Items = loadedAppointments, TotalCount = items.size))
                    ?: AppointmentResponse(NewsData(loadedAppointments, 0), "", 200, true)
            } else {
                (results.firstOrNull { it is NetworkResponse.ApiError } as? NetworkResponse.ApiError)
                    ?.let { error.value = it.body }
            }
            showLoading.value = false
        }
    }

    private fun showDummyAppointments(patientName: String?) {
        lastQuery = null
        hospitalRequest = null
        loadingMore.value = false
        val items = DummyAppointments.items(patientName)
        loadedAppointments.clear()
        loadedAppointments.addAll(items)
        totalAppointments = items.size
        AppointmentModel.value =
            AppointmentResponse(NewsData(loadedAppointments, items.size), "", 200, true)
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

    // TODO(dummy-appointments): dummy appointments are changed locally, never sent to the API
    private fun dummySuccess() = ErrorModel(Any(), null, 200, true, 200)

    fun cancelAppointment(appointmentId: Int, cancelReason: String?=null) {
        if (DummyAppointments.isDummy(appointmentId.toLong())) {
            cancel.value = dummySuccess()
            return
        }
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
        if (DummyAppointments.isDummy(appointmentId.toLong())) {
            changeAppointmentStatusResponse.value = dummySuccess()
            return
        }
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

    companion object {
        /** Per-doctor page for the hospital list (merged, not paged further). */
        private const val HOSPITAL_PAGE_SIZE = 100
    }
}
