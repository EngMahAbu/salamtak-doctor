package com.salamtak.salamtakdoctor.salamtak_doctor.presentation.ui.hospital

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salamtak.domain.entities.response.hospital.HospitalClinicsResponse
import com.salamtak.domain.entities.response.hospital.HospitalDashboardResponse
import com.salamtak.domain.entities.response.hospital.HospitalDoctorsResponse
import com.salamtak.domain.entities.response.hospital.HospitalInfoResponse
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.util.Resource
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class HospitalViewModel : ViewModel() {

    private val repo = HospitalRepository()
    val dashboardResponse = MutableLiveData<Resource<HospitalDashboardResponse>>()
    val hospitalInfoResponse = MutableLiveData<Resource<HospitalInfoResponse>>()
    val clinicsResponse = MutableLiveData<Resource<HospitalClinicsResponse>>()
    val doctorsResponse = MutableLiveData<Resource<HospitalDoctorsResponse>>()

    fun getHospitalDashboard(
        hospitalInfoId: Int,
        dateFrom: String? = null,
        dateTo: String? = null,
        clinicId: Int? = null,
        doctorId: Int? = null
    ) = viewModelScope.launch(Dispatchers.IO + handler(dashboardResponse)) {
        dashboardResponse.postValue(Resource.Loading())
        val response = repo.getHospitalDashboard(hospitalInfoId, dateFrom, dateTo, clinicId, doctorId)
        dashboardResponse.postValue(handle(response))
    }

    fun getHospitalInfoById(infoId: Int) =
        viewModelScope.launch(Dispatchers.IO + handler(hospitalInfoResponse)) {
            hospitalInfoResponse.postValue(Resource.Loading())
            hospitalInfoResponse.postValue(handle(repo.getHospitalInfoById(infoId)))
        }

    fun getHospitalClinicList(hospitalId: Int) =
        viewModelScope.launch(Dispatchers.IO + handler(clinicsResponse)) {
            clinicsResponse.postValue(Resource.Loading())
            val response = handle(repo.getHospitalClinicList(hospitalId))
            val body = response.data
            clinicsResponse.postValue(
                if (body == null) response
                else Resource.sucess(body.copy(Data = body.Data?.filterNot { it.ClinicId in HIDDEN_CLINIC_IDS }))
            )
        }

    /** Doctors of one clinic when [clinicId] is set, otherwise all doctors of the hospital. */
    fun getDoctors(hospitalId: Int, clinicId: Int?) =
        viewModelScope.launch(Dispatchers.IO + handler(doctorsResponse)) {
            doctorsResponse.postValue(Resource.Loading())
            val response = if (clinicId != null) repo.getClinicDoctors(clinicId)
            else repo.getHospitalDoctorList(hospitalId)
            doctorsResponse.postValue(handle(response))
        }

    private fun <T> handle(response: Response<T>): Resource<T> {
        val body = response.body()
        if (response.isSuccessful && body != null) {
            return Resource.sucess(body)
        }
        return Resource.Error(response.message().ifBlank { "Error ${response.code()}" })
    }

    private fun <T> handler(target: MutableLiveData<Resource<T>>) =
        CoroutineExceptionHandler { _, exception ->
            Log.e("HospitalViewModel", "exception: ${exception.message}")
            target.postValue(Resource.Error(exception.message ?: "Error"))
        }

    companion object {
        // Prime Dental, Smile Care and Dental Care: kept out of the All Clinics pickers on request
        private val HIDDEN_CLINIC_IDS = setOf(38914, 38915, 38916)
    }
}
